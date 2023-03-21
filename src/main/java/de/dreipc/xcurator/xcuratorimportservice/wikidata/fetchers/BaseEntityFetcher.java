package de.dreipc.xcurator.xcuratorimportservice.wikidata.fetchers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiDataBaseEntity;

import java.net.URL;
import java.util.List;
import java.util.stream.StreamSupport;

public class BaseEntityFetcher {

    public String query(List<String> ids, String language) {
        var convertIds = ids.stream().map(id -> "wd:" + id).toList();
        var valuesIds = String.join(" ", convertIds);

        return ("""
                SELECT ?entity ?title ?description ?image ?article
                WHERE
                {
                  VALUES ?entity { <ENTITY_IDS> }
                  
                  ?entity rdfs:label ?title.
                  FILTER(lang(?title) = "<LANGUAGE_CODE>")
                  
                  ?entity schema:description ?description.
                  FILTER(lang(?description) = "<LANGUAGE_CODE>")
                  
                    OPTIONAL {
                        ?entity wdt:P18 ?image.
                        ?article schema:about ?entity .
                        ?article schema:inLanguage "<LANGUAGE_CODE>" .
                        FILTER (SUBSTR(str(?article), 1, 25) = "https://<LANGUAGE_CODE>.wikipedia.org/")
                    }
                          
                               
                }
                LIMIT <LIMIT_COUNT>
                """)
                .replace("<ENTITY_IDS>", valuesIds)
                .replace("<LIMIT_COUNT>", String.valueOf(convertIds.size()))
                .replaceAll("<LANGUAGE_CODE>", language);

    }

    public List<WikiDataBaseEntity> convert(ArrayNode jsonArrayNode) {
        return StreamSupport.stream(jsonArrayNode.spliterator(), false)
                .map(node -> {
                    var idUrl = node
                            .get("entity")
                            .get("value")
                            .asText();
                    var id = idUrl.substring(idUrl.lastIndexOf("/") + 1);

                    URL imageUrl = null;
                    try {
                        imageUrl = new URL(node
                                                   .get("image")
                                                   .get("value")
                                                   .asText());
                    } catch (Exception e) {

                    }
                    URL articleUrl = null;
                    try {
                        articleUrl = new URL(node
                                                     .get("article")
                                                     .get("value")
                                                     .asText());
                    } catch (Exception e) {

                    }


                    return WikiDataBaseEntity
                            .builder()
                            .id(id)
                            .name(node
                                          .get("title")
                                          .get("value")
                                          .asText())
                            .description(node
                                                 .get("description")
                                                 .get("value")
                                                 .asText())
                            .articleUrl(articleUrl)
                            .imageUrl(imageUrl)
                            .build();


                }).toList();
    }
}
