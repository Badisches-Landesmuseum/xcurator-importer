package de.dreipc.xcurator.xcuratorimportservice.wikidata;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WikiDataTest {

    private final WikiData wikiData = new WikiData();

    @ParameterizedTest
    @CsvSource({
            "Q142, de, Frankreich",
            "Q142, en, France",
            "Q103801, de, Ostpreußen"
    })
    void entity(String wikiId, String language, String expectedTitle) {
        var entity = wikiData.entity(wikiId, language);
        assertNotNull(entity);
        assertEquals(entity.getName(), expectedTitle);
    }
}