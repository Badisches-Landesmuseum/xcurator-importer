package de.dreipc.xcurator.xcuratorimportservice.wikidata.fetchers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiDataBaseEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.StreamSupport;

public class BaseEntityFetcher {

    public String query(List<String> ids, String language){
        var convertIds = ids.stream().map(id -> "wd:"+id).toList();
        var valuesIds = String.join(" ", convertIds);

        return String.format("""
                              SELECT ?entity ?title ?description ?image
                              WHERE
                              {
                                VALUES ?entity { %s }
                                
                                ?entity rdfs:label ?title.
                                FILTER(lang(?title) = "%s")
                                
                                ?entity schema:description ?description.
                                FILTER(lang(?description) = "%s")
                                
                                ?entity wdt:P18 ?image.
                              }
                              LIMIT %s
                              """, valuesIds, language, language, convertIds.size());
    }

    public List<WikiDataBaseEntity> convert(ArrayNode jsonArrayNode){
        return StreamSupport.stream(jsonArrayNode.spliterator(), false)
                     .map(node -> {
                         try {
                             var imageUrl = new URL(node
                                     .get("image")
                                     .get("value")
                                     .asText());
                             var idUrl = node
                                     .get("entity")
                                     .get("value")
                                     .asText();
                             var id = idUrl.substring(idUrl.lastIndexOf("/") + 1);
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
                                     .imageUrl(imageUrl)
                                     .build();
                         } catch (MalformedURLException e) {
                             throw new RuntimeException(e);
                         }
        }).toList();
    }
}
