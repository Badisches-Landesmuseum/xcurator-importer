package de.dreipc.xcurator.xcuratorimportservice.namedentities;

import com.google.common.collect.Lists;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MissingEntitiesHandler {

    private final TextContentRepository textContentRepository;

    private final NamedEntityRepository namedEntityRepository;

    private final NamedEntitiesRequester requester;

    private final int BATCH_SIZE = 100;


    public MissingEntitiesHandler(TextContentRepository textContentRepository, NamedEntityRepository namedEntityRepository, NamedEntitiesRequester requester) {
        this.textContentRepository = textContentRepository;
        this.namedEntityRepository = namedEntityRepository;
        this.requester = requester;
    }

    public int execute(LanguageCode languageCode) {
        AtomicInteger requested = new AtomicInteger();
        var batches = Lists.partition(textContentRepository.findAllIdsByLanguageCode(languageCode), BATCH_SIZE);
        batches.forEach(textContentIdBatch -> {
                    var existedEntitiesBySourceIds = namedEntityRepository.findAllExistedSourceIds(textContentIdBatch.stream().toList());
                    List<ObjectId> missingTextIds = new ArrayList<>(textContentIdBatch);
                    missingTextIds.removeAll(existedEntitiesBySourceIds);
                    var missingTextContent = textContentRepository.findAllById(missingTextIds);
                    requested.addAndGet(missingTextIds.size());
                    requester.execute(missingTextContent);
                }
        );

        return requested.get();

    }
}
