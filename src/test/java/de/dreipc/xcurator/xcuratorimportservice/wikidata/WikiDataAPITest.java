package de.dreipc.xcurator.xcuratorimportservice.wikidata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WikiDataAPITest {

    private WikiDataAPI wikidata = new WikiDataAPI();
    private final String query = """
            SELECT ?title ?description ?image
            WHERE
            {
              VALUES ?entity { wd:Q142 }
              
              ?entity rdfs:label ?title.
              FILTER(lang(?title) = "de")
              
              ?entity schema:description ?description.
              FILTER(lang(?description) = "de")
              
              ?entity wdt:P18 ?image.
              
            }
            LIMIT 1
            """;

    @Test
    void requestQuery() {
        var response = wikidata.requestQuery(query);
        assertFalse(response.isEmpty());
    }
}