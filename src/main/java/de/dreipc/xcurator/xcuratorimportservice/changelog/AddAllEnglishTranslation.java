package de.dreipc.xcurator.xcuratorimportservice.changelog;

import com.mongodb.BasicDBObject;
import de.dreipc.xcurator.xcuratorimportservice.commands.CreateTextContentCommand;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j

@ChangeUnit(id = "AddAllEnglishTranslation", order = "2", author = "nbabai", runAlways = false)
public class AddAllEnglishTranslation {

    private final String COLLECTION_NAME = "xcurator-text-content";
    private final MongoTemplate mongoTemplate;
    private final int BATCH_SIZE = 100;

    //private final int LIMIT = 20;
    private final CreateTextContentCommand translator;

    public AddAllEnglishTranslation(MongoTemplate mongoTemplate, CreateTextContentCommand translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
    }

    @Execution
    public void execute() {
        // execute(LanguageCode.de, LanguageCode.en);
        //  execute(LanguageCode.nl, LanguageCode.en);

    }

    public void execute(LanguageCode source, LanguageCode target) {
        var searchQuery = new BasicDBObject();
        searchQuery.put("languageCode", source);

        AtomicInteger saved = new AtomicInteger();
        List<TextContent> textContentList = new ArrayList<>();
        mongoTemplate.getCollection(COLLECTION_NAME)
                .find(searchQuery)
                .batchSize(BATCH_SIZE)
                // .limit(LIMIT)
                .forEach(document ->
                        {
                            if (textContentList.size() == BATCH_SIZE) {
                                mongoTemplate.insertAll(textContentList);
                                saved.getAndAdd(textContentList.size());
                                textContentList.clear();

                            } else {
                                try {
                                    Optional<TextContent> optionalText = translator.translateAndCreate(
                                            document.get("projectId", ObjectId.class),
                                            document.get("sourceId", ObjectId.class),
                                            document.get("content").toString(),
                                            TextType.valueOf(document.get("textType").toString()),
                                            target);

                                    textContentList.add(optionalText.get());

                                } catch (Exception e) {
                                    log.error("Did not translate object", e);
                                }
                            }
                        }
                );

        mongoTemplate.insertAll(textContentList);
        saved.getAndAdd(textContentList.size());

        log.info("Saved " + saved + " translated texts in: " + target);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // empty
    }
}
