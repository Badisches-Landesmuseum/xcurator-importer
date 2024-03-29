package de.dreipc.xcurator.xcuratorimportservice.wikipedia;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class WikiPediaBaseEntity {
    String id;
    String name;
    String description;
    URL imageUrl;
    URL articleUrl;
}
