package de.dreipc.xcurator.xcuratorimportservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.elasticsearch.queries.DistributionQueryBuilder;
import de.dreipc.elasticsearch.queries.IdQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryInterrogator;
import de.dreipc.xcurator.xcuratorimportservice.config.ElasticsearchProperties;
import dreipc.graphql.types.MaterialGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MaterialDistributionService {

    private static final Integer CLUSTER_SIZE = 12;
    private static final String FIELD_NAME = "materials";
    private final ElasticsearchProperties properties;
    private final NativeQueryInterrogator nativeQueryInterrogator;

    public MaterialDistributionService(NativeQueryInterrogator nativeQueryInterrogator, ElasticsearchProperties properties) {
        this.nativeQueryInterrogator = nativeQueryInterrogator;
        this.properties = properties;

    }

    public List<MaterialGroup> getDistributions(List<String> withIds) {

        List<MaterialGroup> materialGroups = new ArrayList<>();

        try {
            var distributionQuery = DistributionQueryBuilder.query(CLUSTER_SIZE, FIELD_NAME);
            var query = NativeQueryBuilder.builder().queryBody(distributionQuery).build();
            try {
                query.addQuery(IdQueryBuilder.query(withIds));
            } catch (Exception e) {
            }

            var jsonNode = nativeQueryInterrogator.search(properties.getIndex(), query);
            var listNode = jsonNode.get("aggregations").get("top_clusters").get("buckets");

            var totalDocs = jsonNode.get("hits").get("total").get("value").asInt();
            if (listNode instanceof ArrayNode arrayListNode) {
                materialGroups = StreamSupport
                        .stream(arrayListNode.spliterator(), false)
                        .map(entryNode -> fromJson(entryNode, totalDocs))
                        .toList();
            } else throw new NullPointerException();

        } catch (
                IOException e) {
            log.error("error extracting materials", e);
        }
        return materialGroups;

    }

    public List<MaterialGroup> getLeastDistributions(List<String> withIds) {
        var LEAST_ITEM_COUNT = 3;
        var materialGroups = this.getDistributions(withIds);
        if (materialGroups.isEmpty()) return null;
        if (materialGroups.size() < 3) LEAST_ITEM_COUNT = materialGroups.size() - 1;
        return materialGroups.stream().skip(materialGroups.stream().count() - LEAST_ITEM_COUNT).toList();
    }

    private MaterialGroup fromJson(JsonNode jsonNode, int totalDocs) {
        var count = jsonNode.get("doc_count").asInt();
        var ratio = Math.round((float) count / totalDocs * 100);
        var materialName = jsonNode.get("key").asText();
        return MaterialGroup.newBuilder()
                .name(materialName)
                .count(count)
                .ratio(ratio)
                .build();
    }

}
