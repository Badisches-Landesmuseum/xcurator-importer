package de.dreipc.xcurator.xcuratorimportservice.wikipedia.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import de.dreipc.xcurator.xcuratorimportservice.wikipedia.WikiPediaBaseEntity;

import java.net.URL;

public class WikipediaBaseEntityFetcher {

    public static WikiPediaBaseEntity convert(JsonNode node) {
        URL imageUrl = null;
        try {
            imageUrl = new URL(node
                                       .get("originalimage")
                                       .get("source")
                                       .asText());
        } catch (Exception e) {

        }

        URL articleUrl = null;
        try {
            articleUrl = new URL(node
                                         .get("content_urls")
                                         .get("desktop")
                                         .get("page")
                                         .asText());
        } catch (Exception e) {

            }


            String description = null;
            try {
                description = node
                        .get("extract")
                        .asText();
            } catch (Exception e) {

            }

            return WikiPediaBaseEntity
                    .builder()
                    .description(description)
                    .id(node
                                .get("pageid")
                                .asText())
                    .name(node
                                  .get("title")
                                  .asText())
                    .articleUrl(articleUrl)
                    .imageUrl(imageUrl)
                    .build();
    }
}
