package de.dreipc.xcurator.xcuratorimportservice.services;

import dreipc.graphql.types.MuseumObjectSearchWhereInput;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static graphql.Assert.assertTrue;

@Slf4j
@SpringBootTest(classes = {ArtifactsSearchQuery.class})
class ArtifactsSearchQueryTest {

    @Test
    void testArtifactBooleanQuery() {
        var where = MuseumObjectSearchWhereInput.newBuilder()
                .countries(List.of("Deutschland"))
                .keywords(List.of("Grabfund"))
                .build();

        var query = ArtifactsSearchQuery.boolQuery(where);
        assertTrue(query.contains("term"));
    }

    @Test
    void testArtifactQuery() {
        var where = MuseumObjectSearchWhereInput.newBuilder()
                .countries(List.of("Deutschland"))
                .keywords(List.of("Grabfund"))
                .build();

        var query = ArtifactsSearchQuery.getQuery(where, 10, 1);
        assertTrue(query.contains("term"));
    }


}
