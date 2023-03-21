package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.TextContentDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import dreipc.graphql.types.Language;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class TextContentDataFetcher {

    private final TextContentRepository repository;
    private final NamedEntityRepository namedEntityRepository;

    public TextContentDataFetcher(TextContentRepository repository, NamedEntityRepository namedEntityRepository) {
        this.repository = repository;
        this.namedEntityRepository = namedEntityRepository;
    }


    @DgsData.List({
            @DgsData(parentType = "MuseumObject", field = "title"),
            @DgsData(parentType = "MuseumObject", field = "description")
    })
    public CompletableFuture<TextContent> getTextContent(@NotNull DgsDataFetchingEnvironment env) {
        TextType textType = TextType.valueOf(env.getField().getName().toUpperCase());
        var language = (Language) env.getGraphQlContext().get("preferredLanguage");
        var source = (MuseumObject) env.getSource();
        var dataLoader = env.<ObjectId, TextContent>getDataLoader(TextContentDataLoader.class);
        try {
            var textContext = repository.findTextContentByTextTypeAndSourceIdAndLanguageCode(textType, source.getId(), LanguageCode.getLanguageCode(language)).get();
            return dataLoader.load(textContext.getId());
        } catch (Exception e) {
            var textContext = repository.findTextContentByTextTypeAndSourceIdAndOriginalText(textType, source.getId(), true);
            return dataLoader.load(textContext.get().getId());
        }

    }

    @DgsData(parentType = "TextContent")
    public CompletableFuture<List<NamedEntity>> entities(@NotNull DgsDataFetchingEnvironment env) {
        var source = (TextContent) env.getSource();
        var entities = namedEntityRepository.findAllBySourceId(source.getId());
        if (entities == null | entities.size() == 0)
            return CompletableFuture.supplyAsync(ArrayList::new);
        return CompletableFuture.supplyAsync(() -> entities);
    }
}
