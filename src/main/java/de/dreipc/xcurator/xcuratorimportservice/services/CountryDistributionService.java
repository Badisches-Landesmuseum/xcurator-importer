package de.dreipc.xcurator.xcuratorimportservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.elasticsearch.queries.DistributionQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryInterrogator;
import de.dreipc.xcurator.xcuratorimportservice.config.ElasticsearchProperties;
import dreipc.graphql.types.CountryGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class CountryDistributionService {

    private static final String FIELD_NAME = "countryName";
    private static final Integer CLUSTER_SIZE = 24;
    private final NativeQueryInterrogator nativeQueryInterrogator;

    private final ElasticsearchProperties elasticsearchProperties;

    public CountryDistributionService(NativeQueryInterrogator nativeQueryInterrogator, ElasticsearchProperties elasticsearchProperties) {
        this.nativeQueryInterrogator = nativeQueryInterrogator;
        this.elasticsearchProperties = elasticsearchProperties;
    }

    public List<CountryGroup> getDistributions() {

        List<CountryGroup> countryGroup = new ArrayList<>();

        try {
            var distributionQuery = DistributionQueryBuilder.query(CLUSTER_SIZE, FIELD_NAME);
            var query = NativeQueryBuilder.builder().queryBody(distributionQuery).build();
            var jsonNode = nativeQueryInterrogator.search(elasticsearchProperties.getIndex(), query);
            var listNode = jsonNode.get("aggregations").get("top_clusters").get("buckets");

            var totalDocs = jsonNode.get("hits").get("total").get("value").asInt();
            if (listNode instanceof ArrayNode arrayListNode) {
                countryGroup = StreamSupport
                        .stream(arrayListNode.spliterator(), false)
                        .map(entryNode -> fromJson(entryNode, totalDocs))
                        .toList();
            } else throw new NullPointerException();

        } catch (
                IOException e) {
            log.error("error extracting country distribution", e);
        }
        return countryGroup;

    }

    private CountryGroup fromJson(JsonNode jsonNode, int totalDocs) {
        var count = jsonNode.get("doc_count").asInt();
        var ratio = Math.round((float) count / totalDocs * 100);
        var countryName = jsonNode.get("key").asText();
        return CountryGroup.newBuilder()
                .name(countryName)
                .count(count)
                .ratio(ratio)
                .build();
    }

}
