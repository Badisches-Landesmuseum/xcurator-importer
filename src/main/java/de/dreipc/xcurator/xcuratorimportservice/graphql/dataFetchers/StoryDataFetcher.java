package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.*;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.ModuleDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.StoryDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import de.dreipc.xcurator.xcuratorimportservice.repositories.ModuleRepository;
import dreipc.graphql.types.StoryUniqueInput;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class StoryDataFetcher {

    private final ModuleRepository moduleRepository;

    public StoryDataFetcher(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @DgsEntityFetcher(name = "Story")
    public CompletableFuture<Story> getStory(Map<String, Object> values, DgsDataFetchingEnvironment env) {
        var dataLoader = env.<ObjectId, Story>getDataLoader(StoryDataLoader.class);
        return dataLoader.load(new ObjectId(values.get("id").toString()));
    }


    @DgsQuery
    public CompletableFuture<Story> story(@InputArgument StoryUniqueInput where, DgsDataFetchingEnvironment env) {
        var id = new ObjectId(where.getId());
        var dataLoader = env.<ObjectId, Story>getDataLoader(StoryDataLoader.class);
        return dataLoader.load(id);

    }

    @DgsData(parentType = "Story")
    public CompletableFuture<List<Module>> modules(@NotNull DgsDataFetchingEnvironment env) {
        var source = (Story) env.getSource();
        var relatedIds = moduleRepository.findAllIdsByStoryId(source.getId());
        var dataLoader = env.<ObjectId, Module>getDataLoader(ModuleDataLoader.class);
        if (relatedIds == null)
            return CompletableFuture.supplyAsync(ArrayList::new);
        return dataLoader.loadMany(relatedIds);
    }

}
