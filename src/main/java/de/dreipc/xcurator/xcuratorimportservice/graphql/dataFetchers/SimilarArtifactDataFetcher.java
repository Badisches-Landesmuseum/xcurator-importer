package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.SimilarArtefactService;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.ImageMuseumObjectSearchWhereInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@DgsComponent
public class SimilarArtifactDataFetcher {

    private final SimilarArtefactService similarArtefactService;
    private final MuseumObjectRepository museumObjectRepository;

    public SimilarArtifactDataFetcher(SimilarArtefactService similarArtefactService, MuseumObjectRepository museumObjectRepository) {
        this.similarArtefactService = similarArtefactService;
        this.museumObjectRepository = museumObjectRepository;
    }


    @DgsQuery
    public CompletableFuture<CountConnection<MuseumObject>> similarMuseumObjects(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            @InputArgument ImageMuseumObjectSearchWhereInput where,
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            return similarArtefactService.getSimilar(where.getImageId(), where.getProjectId(), first, skip, environment);
        });

    }

}
