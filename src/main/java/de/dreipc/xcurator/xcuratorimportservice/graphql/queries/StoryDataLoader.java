package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import de.dreipc.xcurator.xcuratorimportservice.repositories.StoryRepository;
import org.bson.types.ObjectId;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DgsDataLoader(name = "stories")
public class StoryDataLoader implements MappedBatchLoader<ObjectId, Story> {

    private final StoryRepository repository;
    private final Executor executor;

    public StoryDataLoader(StoryRepository repository, Executor executor) {
        this.repository = repository;
        this.executor = executor;
    }

    @Override
    public CompletionStage<Map<ObjectId, Story>> load(Set<ObjectId> ids) {
        return CompletableFuture.supplyAsync(() ->
                        StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
                                .collect(Collectors.toMap(Story::getId, object -> object))
                , executor);
    }

}
