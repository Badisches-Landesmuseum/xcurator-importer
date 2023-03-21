package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import de.dreipc.xcurator.xcuratorimportservice.services.CountryDistributionService;
import dreipc.graphql.types.CountryGroup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class CountryDistributionDataFetcher {


    private final CountryDistributionService distributionService;

    public CountryDistributionDataFetcher(CountryDistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @DgsQuery
    public CompletableFuture<List<CountryGroup>> countryDistribution(
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            return distributionService.getDistributions();
        });
    }


}
