package de.dreipc.xcurator.xcuratorimportservice.graphql.mutations;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreStoryCommand;
import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import dreipc.graphql.types.CreateStory;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class StoryCommandResolver {

    private final StoreStoryCommand storeStoryCommand;

    public StoryCommandResolver(StoreStoryCommand storeStoryCommand) {
        this.storeStoryCommand = storeStoryCommand;
    }

    @DgsMutation
    public Story createStory(@InputArgument CreateStory create) {
        return storeStoryCommand.execute(create);
    }


}
