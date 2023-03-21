package de.dreipc.xcurator.xcuratorimportservice.wikidata;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class WikiDataBaseEntity {
    String id;
    String name;
    String description;
    URL imageUrl;
    URL articleUrl;
}
