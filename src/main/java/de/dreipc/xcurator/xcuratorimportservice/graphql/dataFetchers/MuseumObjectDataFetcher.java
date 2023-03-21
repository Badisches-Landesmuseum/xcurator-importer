package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.*;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.MuseumObjectDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import dreipc.graphql.types.Image;
import dreipc.graphql.types.Language;
import dreipc.graphql.types.MuseumObjectUniqueInput;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class MuseumObjectDataFetcher {

    private final TopicRepository topicRepository;

    public MuseumObjectDataFetcher(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @DgsEntityFetcher(name = "MuseumObject")
    public CompletableFuture<MuseumObject> getMuseumObject(@NonNull Map<String, Object> values, @NotNull DgsDataFetchingEnvironment env) {
        var dataLoader = env.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
        return dataLoader.load(new ObjectId(values.get("id").toString()));
    }


    @DgsQuery
    public CompletableFuture<MuseumObject> museumObject(@InputArgument MuseumObjectUniqueInput where, @InputArgument Language preferredLanguage, @NotNull DgsDataFetchingEnvironment env) {
        var id = new ObjectId(where.getId());
        if (preferredLanguage != null) env.getGraphQlContext().put("preferredLanguage", preferredLanguage);
        var dataLoader = env.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
        return dataLoader.load(id);

    }


    @DgsData(parentType = "MuseumObject", field = "images")
    public CompletableFuture<List<Image>> museumImages(@NotNull DgsDataFetchingEnvironment env) {
        var source = (MuseumObject) env.getSource();
        var relatedIds = source.getAssetIds();
        if (relatedIds == null)
            return CompletableFuture.supplyAsync(ArrayList::new);
        var images = relatedIds.stream().map(id -> Image.newBuilder().id(id.toString()).build()).toList();
        return CompletableFuture.supplyAsync(() -> images);
    }

    @DgsData(parentType = "MuseumObject")
    public CompletableFuture<List<MuseumObjectTopic>> topics(@NotNull DgsDataFetchingEnvironment env) {
        var source = (MuseumObject) env.getSource();
        var topics = topicRepository.findAllBySourceId(source.getId());
        if (topics == null)
            return CompletableFuture.supplyAsync(ArrayList::new);
        return CompletableFuture.supplyAsync(() -> topics);
    }

}
