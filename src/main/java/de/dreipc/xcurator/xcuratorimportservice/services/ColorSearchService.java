package de.dreipc.xcurator.xcuratorimportservice.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.elasticsearch.queries.NativeQueryInterrogator;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.utils.StreamUtil;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.MuseumObjectColorSearchResult;
import graphql.schema.DataFetchingEnvironment;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

@Service
public class ColorSearchService {

    private static final String COLOR_INDEX = "colors";
    private final NativeQueryInterrogator nativeQueryInterrogator;

    private final MuseumObjectRepository museumObjectRepository;

    public ColorSearchService(NativeQueryInterrogator nativeQueryInterrogator, MuseumObjectRepository museumObjectRepository) {
        this.nativeQueryInterrogator = nativeQueryInterrogator;
        this.museumObjectRepository = museumObjectRepository;
    }

    public CountConnection<MuseumObjectColorSearchResult> artefactsByColor(String hexColor, int first, int skip, DataFetchingEnvironment environment) {

        var artifactIds = new ArrayList<ObjectId>();

        var totalCount = 0;
        try {
            var stringQuery = ColorSearchQuery.query(hexColor, first, skip);
            var jsonNode = nativeQueryInterrogator.search(COLOR_INDEX, stringQuery);

            totalCount = jsonNode
                    .get("hits")
                    .get("total")
                    .get("value")
                    .asInt();

            var resultHits = jsonNode
                    .get("hits")
                    .get("hits");

            if (resultHits instanceof ArrayNode results) {
                StreamSupport.stream(results.spliterator(), false).forEach(result -> {
                    var artefactId = result.get("_source").get("artefactId").asText();
                    artifactIds.add(new ObjectId(artefactId));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        var results = StreamUtil.stream(museumObjectRepository.findAllById(artifactIds)).toList();

        if (results.isEmpty())
            return CountConnection.empty();

        var artifacts = results.stream().map(artifact ->
                MuseumObjectColorSearchResult.newBuilder()
                        .museumObject(artifact)
                        .build()
        ).toList();


        return new CountConnection<>(artifacts, totalCount, environment);
    }
}
