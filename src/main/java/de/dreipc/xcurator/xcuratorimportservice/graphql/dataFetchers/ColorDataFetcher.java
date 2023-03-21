package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.services.ColorSearchService;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.MuseumObjectColorSearchResult;
import dreipc.graphql.types.MuseumObjectColorSearchWhereInput;

import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ColorDataFetcher {

    private final ColorSearchService colorSearchService;

    public ColorDataFetcher(ColorSearchService colorSearchService) {
        this.colorSearchService = colorSearchService;
    }


    @DgsQuery
    private CompletableFuture<CountConnection<MuseumObjectColorSearchResult>> museumObjectsByColor(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            @InputArgument MuseumObjectColorSearchWhereInput where,
            DgsDataFetchingEnvironment environment
    ) {
        return CompletableFuture.supplyAsync(() -> {
            var hexColor = where.getColor();
            return colorSearchService.artefactsByColor(hexColor, first, skip, environment);
        });
    }

}
