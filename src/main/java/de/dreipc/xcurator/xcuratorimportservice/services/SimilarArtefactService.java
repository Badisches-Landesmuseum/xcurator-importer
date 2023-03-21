package de.dreipc.xcurator.xcuratorimportservice.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import de.dreipc.elasticsearch.queries.NativeQueryInterrogator;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObjectCountConnection;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.utils.StreamUtil;
import dreipc.common.graphql.relay.CountConnection;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class SimilarArtefactService {

    private final NativeQueryInterrogator nativeQueryInterrogator;

    private final MuseumObjectRepository museumObjectRepository;

    public SimilarArtefactService(NativeQueryInterrogator nativeQueryInterrogator, MuseumObjectRepository museumObjectRepository) {
        this.nativeQueryInterrogator = nativeQueryInterrogator;
        this.museumObjectRepository = museumObjectRepository;
    }

    public CountConnection<MuseumObject> getSimilar(String imageId, String projectId, int first, int skip, DgsDataFetchingEnvironment environment) {
        List<ObjectId> artifactIds = new ArrayList<>();


        var totalCount = 0;
        try {
            var similarQuery = SimilarArtefactQuery.query(imageId, projectId, first, skip);
            var indexName = SimilarArtefactQuery.indexName(projectId);
            var jsonNode = nativeQueryInterrogator.search(indexName, similarQuery);

            totalCount = jsonNode
                    .get("hits")
                    .get("total")
                    .get("value")
                    .asInt();

            var resultHits = jsonNode
                    .get("hits")
                    .get("hits");
            if (resultHits instanceof ArrayNode arrayListNode) {
                StreamSupport
                        .stream(arrayListNode.spliterator(), false)
                        .forEach(resultJson -> {
                            try {
                                var artefactId = resultJson
                                        .get("fields")
                                        .get("reference_id")
                                        .get(0)
                                        .asText("");

                                artifactIds.add(new ObjectId(artefactId));

                            } catch (Exception e) {
                                // empty
                            }
                        });
            }
        } catch (
                IOException e) {
            log.error("error get similar museumObjects", e);
        }

        var results = StreamUtil.stream(museumObjectRepository.findAllById(artifactIds)).toList();
        if (results.isEmpty())
            return CountConnection.empty();

        return new MuseumObjectCountConnection<>(results, totalCount, environment);
    }
}
