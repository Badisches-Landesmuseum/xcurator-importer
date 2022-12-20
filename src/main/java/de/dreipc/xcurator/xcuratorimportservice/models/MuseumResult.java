package de.dreipc.xcurator.xcuratorimportservice.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MuseumResult {
    MuseumObject museumObject;
    List<MuseumImage> images;
    List<TextContent> texts;
}
