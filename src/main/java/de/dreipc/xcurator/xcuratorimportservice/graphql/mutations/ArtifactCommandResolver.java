package de.dreipc.xcurator.xcuratorimportservice.graphql.mutations;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import de.dreipc.xcurator.xcuratorimportservice.commands.SyncArtifactCommand;
import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndexCreator;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class ArtifactCommandResolver {

    private final SyncArtifactCommand syncArtifactCommand;

    private final ArtifactIndexCreator artifactIndexCreator;

    public ArtifactCommandResolver(SyncArtifactCommand syncArtifactCommand, ArtifactIndexCreator artifactIndexCreator) {
        this.syncArtifactCommand = syncArtifactCommand;
        this.artifactIndexCreator = artifactIndexCreator;
    }

    @DgsMutation
    public Integer sync() {
        return syncArtifactCommand.execute();
    }


    @DgsMutation
    public int createIndexes() {
        return artifactIndexCreator.execute();
    }

}
