package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import org.bson.types.ObjectId;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DgsDataLoader(name = "textContent")
public class TextContentDataLoader implements MappedBatchLoader<ObjectId, TextContent> {

    private final TextContentRepository repository;
    private final Executor executor;

    public TextContentDataLoader(TextContentRepository repository, Executor executor) {
        this.repository = repository;
        this.executor = executor;
    }

    @Override
    public CompletionStage<Map<ObjectId, TextContent>> load(Set<ObjectId> ids) {
        return CompletableFuture.supplyAsync(() ->
                        StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
                                .collect(Collectors.toMap(TextContent::getId, object -> object))
                , executor);
    }

}
