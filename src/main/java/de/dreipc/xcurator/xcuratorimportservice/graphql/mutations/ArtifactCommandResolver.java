package de.dreipc.xcurator.xcuratorimportservice.graphql.mutations;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import de.dreipc.xcurator.xcuratorimportservice.commands.SyncArtifactCommand;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class ArtifactCommandResolver {

    private final SyncArtifactCommand syncArtifactCommand;

    public ArtifactCommandResolver(SyncArtifactCommand syncArtifactCommand) {
        this.syncArtifactCommand = syncArtifactCommand;
    }

    @DgsMutation
    public Integer sync() {
        return syncArtifactCommand.execute();
    }
    
}
