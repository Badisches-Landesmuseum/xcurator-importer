package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.elasticsearch.services.UniqueValuesService;
import de.dreipc.xcurator.xcuratorimportservice.config.ElasticsearchProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@DgsComponent
public class SmartKeywordsDataFetcher {

    private final UniqueValuesService uniqueValuesService;

    private final ElasticsearchProperties properties;

    public SmartKeywordsDataFetcher(UniqueValuesService uniqueValuesService, ElasticsearchProperties properties) {
        this.uniqueValuesService = uniqueValuesService;
        this.properties = properties;
    }

    @DgsQuery
    public CompletableFuture<List<String>> smartKeywords(
            @InputArgument Integer first,
            @InputArgument Integer skip,
            DgsDataFetchingEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            var topics = uniqueValuesService.getUniqueValues("topics", 220, properties.getIndex());
            var keywords = uniqueValuesService.getUniqueValues("keywords", 220, properties.getIndex());

            var smartKeywords = new ArrayList<String>();
            smartKeywords.addAll(topics);
            smartKeywords.addAll(keywords);

            return smartKeywords.stream().limit(first).skip(skip).toList();

        });
    }

}
