package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.repositories.ModuleRepository;
import org.bson.types.ObjectId;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DgsDataLoader(name = "modules")
public class ModuleDataLoader implements MappedBatchLoader<ObjectId, Module> {

    private final ModuleRepository repository;
    private final Executor executor;

    public ModuleDataLoader(ModuleRepository repository, Executor executor) {
        this.repository = repository;
        this.executor = executor;
    }

    @Override
    public CompletionStage<Map<ObjectId, Module>> load(Set<ObjectId> ids) {
        return CompletableFuture.supplyAsync(() ->
                        StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
                                .collect(Collectors.toMap(Module::getId, object -> object))
                , executor);
    }

}
