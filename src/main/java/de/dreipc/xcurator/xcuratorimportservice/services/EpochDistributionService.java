package de.dreipc.xcurator.xcuratorimportservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.elasticsearch.queries.DistributionQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryBuilder;
import de.dreipc.elasticsearch.queries.NativeQueryInterrogator;
import de.dreipc.xcurator.xcuratorimportservice.config.ElasticsearchProperties;
import de.dreipc.xcurator.xcuratorimportservice.models.Epoch;
import de.dreipc.xcurator.xcuratorimportservice.models.EpochGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class EpochDistributionService {

    private static final String FIELD_NAME = "epoch";
    private static final Integer CLUSTER_SIZE = 24;
    private static final List<Epoch> epochs = List.of(
            new Epoch("", Integer.MIN_VALUE, Integer.MIN_VALUE),
            new Epoch("Ur - und Frühgeschichte", Integer.MIN_VALUE, -3000),
            new Epoch("Antike", -3000, 565),
            new Epoch("Frühes Mittelalter", 565, 950),
            new Epoch("Romanik", 950, 1200),
            new Epoch("Gotik", 1200, 1400),
            new Epoch("Renaissance", 1400, 1575),
            new Epoch("Barock", 1575, 1770),
            new Epoch("Romantik", 1770, 1855),
            new Epoch("Moderne", 1855, 1945),
            new Epoch("Postmoderne", 1945, Integer.MAX_VALUE)
    );
    private final ElasticsearchProperties properties;
    private final NativeQueryInterrogator nativeQueryInterrogator;

    public EpochDistributionService(ElasticsearchProperties properties, NativeQueryInterrogator nativeQueryInterrogator) {
        this.properties = properties;
        this.nativeQueryInterrogator = nativeQueryInterrogator;
    }

    public static Optional<Epoch> byName(String name) {
        return epochs
                .stream()
                .filter(epoch -> epoch
                        .name()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

    public List<EpochGroup> getDistributions() {

        List<EpochGroup> list = new ArrayList<>();

        try {
            var distributionQuery = DistributionQueryBuilder.query(CLUSTER_SIZE, FIELD_NAME);
            var query = NativeQueryBuilder.builder().queryBody(distributionQuery).build();
            var jsonNode = nativeQueryInterrogator.search(properties.getIndex(), query);
            var listNode = jsonNode.get("aggregations").get("top_clusters").get("buckets");

            var totalDocs = jsonNode.get("hits").get("total").get("value").asInt();
            if (listNode instanceof ArrayNode arrayListNode) {
                list = StreamSupport
                        .stream(arrayListNode.spliterator(), false)
                        .map(entryNode -> fromJson(entryNode, totalDocs))
                        .toList();
            } else throw new NullPointerException();

        } catch (
                IOException e) {
            log.error("error extracting country distribution", e);
        }
        return list;

    }

    private EpochGroup fromJson(JsonNode jsonNode, int totalDocs) {
        var count = jsonNode.get("doc_count").asInt();
        var ratio = Math.round((float) count / totalDocs * 100);
        var countryName = jsonNode.get("key").asText();
        return EpochGroup.builder()
                .name(countryName)
                .count(count)
                .ratio(ratio)
                .build();
    }


}
