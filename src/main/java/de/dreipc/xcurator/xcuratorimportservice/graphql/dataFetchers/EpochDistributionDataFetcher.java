package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import de.dreipc.xcurator.xcuratorimportservice.models.EpochGroup;
import de.dreipc.xcurator.xcuratorimportservice.services.EpochDistributionService;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class EpochDistributionDataFetcher {


    private final EpochDistributionService distributionService;

    public EpochDistributionDataFetcher(EpochDistributionService distributionService) {
        this.distributionService = distributionService;
    }


    @DgsQuery
    public CompletableFuture<List<EpochGroup>> epochDistribution(
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            return distributionService.getDistributions();
        });
    }


    @DgsData(parentType = "EpochGroup")
    public CompletableFuture<Integer> start(@NotNull DgsDataFetchingEnvironment dfe) {
        var epochView = (EpochGroup) dfe.getSource();
        var epoch = EpochDistributionService.byName(epochView.name()).orElseThrow(() -> new IllegalArgumentException("Epoch with name (" + epochView.name() + " ) is unknown."));
        return CompletableFuture.supplyAsync(epoch::start);
    }

    @DgsData(parentType = "EpochGroup")
    public CompletableFuture<Integer> end(@NotNull DgsDataFetchingEnvironment dfe) {
        var epochView = (EpochGroup) dfe.getSource();
        var epoch = EpochDistributionService.byName(epochView.name()).orElseThrow(() -> new IllegalArgumentException("Epoch with name (" + epochView.name() + " ) is unknown."));
        return CompletableFuture.supplyAsync(() -> {
            var end = epoch.end();
            if (end.equals(Integer.MAX_VALUE))
                end = Calendar.getInstance().get(Calendar.YEAR);
            return end;
        });
    }


}
