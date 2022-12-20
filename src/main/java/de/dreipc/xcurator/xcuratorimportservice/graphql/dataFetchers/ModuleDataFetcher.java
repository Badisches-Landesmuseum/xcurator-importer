package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.ModuleDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.MuseumObjectDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ModuleDataFetcher {


    public ModuleDataFetcher() {
    }

    @DgsEntityFetcher(name = "Module")
    public CompletableFuture<Module> getModule(Map<String, Object> values, DgsDataFetchingEnvironment env) {
        var dataLoader = env.<ObjectId, Module>getDataLoader(ModuleDataLoader.class);
        return dataLoader.load(new ObjectId(values.get("id").toString()));
    }



    @DgsData(parentType = "Module")
    public CompletableFuture<MuseumObject> museumObject(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (Module) dfe.getSource();
        var dataLoader = environment.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
        return dataLoader.load(source.getMuseumObjectId());
    }

}
