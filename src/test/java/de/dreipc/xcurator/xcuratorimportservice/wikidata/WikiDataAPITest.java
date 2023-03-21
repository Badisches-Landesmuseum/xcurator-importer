package de.dreipc.xcurator.xcuratorimportservice.wikidata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Wikidata Query Service
 * {@see https://query.wikidata.org/}
 */
class WikiDataAPITest {

    private final WikiDataAPI wikidata = new WikiDataAPI();
    private final String query = """
            SELECT ?title ?description ?image ?article
            WHERE
            {
              VALUES ?entity { wd:Q142 }
             
              ?entity rdfs:label ?title.
              FILTER(lang(?title) = "de")

              ?entity schema:description ?description.
              FILTER(lang(?description) = "de")
              ?entity wdt:P18 ?image.
              
            OPTIONAL {
                ?article schema:about ?entity .
                ?article schema:inLanguage "de" .
                FILTER (SUBSTR(str(?article), 1, 25) = "https://de.wikipedia.org/")
                }
            }
            LIMIT 1

                        """;

    @Test
    void requestQuery() {
        var response = wikidata.requestQuery(query);
        assertFalse(response.isEmpty());
    }
}
