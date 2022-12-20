package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import org.bson.types.ObjectId;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DgsDataLoader(name = "museumObjects")
public class MuseumObjectDataLoader implements MappedBatchLoader<ObjectId, MuseumObject> {

    private final MuseumObjectRepository repository;
    private final Executor executor;

    public MuseumObjectDataLoader(MuseumObjectRepository repository, Executor executor) {
        this.repository = repository;
        this.executor = executor;
    }

    @Override
    public CompletionStage<Map<ObjectId, MuseumObject>> load(Set<ObjectId> ids) {
        return CompletableFuture.supplyAsync(() ->
                        StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
                                .collect(Collectors.toMap(MuseumObject::getId, object -> object))
                , executor);
    }

}
