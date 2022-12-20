package de.dreipc.xcurator.xcuratorimportservice.graphql.mutations;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreModuleCommand;
import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import dreipc.graphql.types.ConnectStory;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class ModuleCommandResolver {

    private final StoreModuleCommand storeModuleCommand;

    public ModuleCommandResolver(StoreModuleCommand storeModuleCommand) {
        this.storeModuleCommand = storeModuleCommand;
    }

    @DgsMutation
    public Module createModule(@InputArgument ConnectStory connect, @InputArgument String museumObjectsId) {
        return storeModuleCommand.execute(new ObjectId(museumObjectsId), new ObjectId(connect.getStoryId()));
    }




}
