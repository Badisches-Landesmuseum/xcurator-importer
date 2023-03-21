package de.dreipc.xcurator.xcuratorimportservice.wikipedia;

import dreipc.graphql.types.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class WikiPediaAPITest {

    private final WikiPediaAPI wikipedia = new WikiPediaAPI();

    @Test
    void requestQuery() {
        var response = wikipedia.requestQuery("Frankreich", Language.DE.toString().toLowerCase());
        assertFalse(response.isEmpty());
    }

    @Test
    void requestUnknownQuery() {
        var response = wikipedia.requestQuery("Titel der nicht existiert", Language.DE.toString().toLowerCase());
        assertFalse(response.isEmpty());
    }

}
