package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.*;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.MuseumObjectDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.TextContentDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.EpochService;
import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import dreipc.graphql.types.Image;
import dreipc.graphql.types.MuseumObjectUniqueInput;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class MuseumObjectDataFetcher {

    private final MuseumObjectRepository repository;
    private final EpochService epochService;
    private final TopicRepository topicRepository;

    private final TextContentRepository textContentRepository;

    public MuseumObjectDataFetcher(MuseumObjectRepository repository, EpochService epochService, TopicRepository topicRepository, TextContentRepository textContentRepository) {
        this.repository = repository;
        this.epochService = epochService;
        this.topicRepository = topicRepository;
        this.textContentRepository = textContentRepository;
    }

    @DgsEntityFetcher(name = "MuseumObject")
    public CompletableFuture<MuseumObject> getMuseumObject(Map<String, Object> values, DgsDataFetchingEnvironment env) {
        var dataLoader = env.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
        return dataLoader.load(new ObjectId(values.get("id").toString()));
    }


    @DgsQuery
    public CompletableFuture<MuseumObject> museumObject(@InputArgument MuseumObjectUniqueInput where, DgsDataFetchingEnvironment env) {
        var id = new ObjectId(where.getId());
        var dataLoader = env.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
        return dataLoader.load(id);

    }

    @DgsData(parentType = "MuseumObject")
    public CompletableFuture<TextContent> title(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (MuseumObject) dfe.getSource();
        var relatedIds = textContentRepository.findAllIdsBySourceIdAndTextType(source.getId(), TextType.TITLE);
        if (relatedIds == null || relatedIds.size() == 0)
            return null;
        var dataLoader = environment.<ObjectId, TextContent>getDataLoader(TextContentDataLoader.class);
        return dataLoader.load(relatedIds.get(0));
    }

    @DgsData(parentType = "MuseumObject")
    public CompletableFuture<TextContent> description(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (MuseumObject) dfe.getSource();
        var relatedIds = textContentRepository.findAllIdsBySourceIdAndTextType(source.getId(), TextType.DESCRIPTION);
        var dataLoader = environment.<ObjectId, TextContent>getDataLoader(TextContentDataLoader.class);

        if (relatedIds == null || relatedIds.size() == 0)
            return null;
        return dataLoader.load(relatedIds.get(0));
    }

    @DgsData(parentType = "MuseumObject", field = "images")
    public CompletableFuture<List<Image>> museumImages(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (MuseumObject) dfe.getSource();
        var relatedIds = source.getAssetIds();
        if (relatedIds == null)
            return CompletableFuture.supplyAsync(ArrayList::new);
        var images = relatedIds.stream().map(id -> Image.newBuilder().id(id.toString()).build()).toList();
        return CompletableFuture.supplyAsync(() -> images);
    }

    @DgsData(parentType = "MuseumObject")
    public CompletableFuture<List<MuseumObjectTopic>> topics(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (MuseumObject) dfe.getSource();
        var topics = topicRepository.findAllBySourceId(source.getId());

        if (topics == null)
            return CompletableFuture.supplyAsync(ArrayList::new);
        return CompletableFuture.supplyAsync(() -> topics);
    }


}
