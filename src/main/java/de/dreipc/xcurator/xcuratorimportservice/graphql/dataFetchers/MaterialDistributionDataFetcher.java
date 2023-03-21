package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import de.dreipc.xcurator.xcuratorimportservice.services.MaterialDistributionService;
import dreipc.graphql.types.MaterialGroup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class MaterialDistributionDataFetcher {


    private final MaterialDistributionService materialDistributionService;

    public MaterialDistributionDataFetcher(MaterialDistributionService materialDistributionService) {
        this.materialDistributionService = materialDistributionService;
    }

    @DgsQuery
    public CompletableFuture<List<MaterialGroup>> materialDistribution(
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            return materialDistributionService.getDistributions(null);
        });
    }

    
}
