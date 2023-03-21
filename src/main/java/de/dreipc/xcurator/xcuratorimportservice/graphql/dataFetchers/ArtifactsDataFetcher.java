package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndexRepository;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.MuseumObjectDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObjectCountConnection;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.MaterialDistributionService;
import de.dreipc.xcurator.xcuratorimportservice.utils.StreamUtil;
import dreipc.graphql.types.*;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ArtifactsDataFetcher {


    private final ArtifactIndexRepository artifactIndexRepository;
    private final MuseumObjectRepository museumObjectRepository;

    private final MaterialDistributionService materialDistributionService;


    public ArtifactsDataFetcher(ArtifactIndexRepository artifactIndexRepository, MuseumObjectRepository museumObjectRepository, MaterialDistributionService materialDistributionService) {

        this.artifactIndexRepository = artifactIndexRepository;
        this.museumObjectRepository = museumObjectRepository;

        this.materialDistributionService = materialDistributionService;
    }

    @DgsQuery
    public CompletableFuture<MuseumObjectCountConnection<MuseumObject>> museumObjects(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            @InputArgument MuseumObjectSearchWhereInput where,
            @InputArgument List<MuseumObjectSearchOrderByInput> orderBy,
            @InputArgument Language preferredLanguage,
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            if (preferredLanguage != null) environment.getGraphQlContext().put("preferredLanguage", preferredLanguage);
            var indexes = artifactIndexRepository.searchMuseumObjects(where, orderBy, first, skip, environment);
            var ids = indexes.getEdges().stream().map(index -> new ObjectId(index.getNode().getId())).toList();
            var repository = StreamUtil.stream(museumObjectRepository.findAllById(ids)).toList();

            return new MuseumObjectCountConnection<>(repository, indexes.getTotalCount(), environment, indexes.getKeywords());

        });
    }


    @DgsQuery
    public CompletableFuture<MuseumObjectCountConnection<ExceptionalMuseumObject>> exceptionalMuseumObjects(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            @InputArgument MuseumObjectSearchWhereInput where,
            @InputArgument List<MuseumObjectSearchOrderByInput> orderBy,
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            var dataLoader = environment.<ObjectId, MuseumObject>getDataLoader(MuseumObjectDataLoader.class);
            var indexes = artifactIndexRepository.searchMuseumObjects(where, orderBy, 1500, 0, environment);
            var resultIds = indexes.getEdges()
                    .stream()
                    .map(indexEdge -> indexEdge.getNode().getId())
                    .toList();

            var minMaterial = materialDistributionService.getDistributions(resultIds).stream().sorted(Comparator.comparing(MaterialGroup::getCount)).findFirst().get();
            where.setMaterials(List.of(minMaterial.getName()));
            indexes = artifactIndexRepository.searchMuseumObjects(where, orderBy, first, skip, environment);

            var ids = indexes.getEdges().stream().map(index -> new ObjectId(index.getNode().getId())).toList();
            var repository = StreamUtil.stream(museumObjectRepository.findAllById(ids)).toList();

            var results = repository.stream().map(artifact -> ExceptionalMuseumObject.newBuilder().museumObject(artifact).reason(List.of(ExceptionalReason.MATERIAL)).build()).toList();

            return new MuseumObjectCountConnection<>(results, indexes.getTotalCount(), environment, indexes.getKeywords());
        });
    }
}
