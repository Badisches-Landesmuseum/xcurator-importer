package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import de.dreipc.xcurator.xcuratorimportservice.repositories.StoryRepository;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.StoryOrderByInput;
import dreipc.graphql.types.StoryWhereInput;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class StoriesDatafetcher {

    private final StoryRepository repository;


    public StoriesDatafetcher(StoryRepository repository) {
        this.repository = repository;
    }

    @DgsQuery(field = "stories")
    public CompletableFuture<CountConnection<Story>> getAssets(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            @InputArgument StoryWhereInput where,
            @InputArgument List<StoryOrderByInput> orderBy,
            DataFetchingEnvironment environment) {

        return CompletableFuture.supplyAsync(() ->
                repository.searchStories(first, skip, where, orderBy, environment)
        );
    }


}
