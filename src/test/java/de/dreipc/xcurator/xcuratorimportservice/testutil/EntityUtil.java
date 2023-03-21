package de.dreipc.xcurator.xcuratorimportservice.testutil;

import de.dreipc.xcurator.xcuratorimportservice.models.*;
import org.bson.types.ObjectId;

import java.time.Instant;

public class EntityUtil {


    public static MuseumObject createMuseumObject() {

        return MuseumObject.builder()
                .id(new ObjectId())
                .externalId(new ObjectId().toString())
                .projectId(new ObjectId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .dateRange(DateRange.builder().build())
                .location(Location.builder().build())
                .dataSource(DataSource.EXPODB)
                .build();
    }


    public static TextContent createTextContent(String content, LanguageCode languageCode) {
        return TextContent.builder()
                .id(new ObjectId())
                .textType(TextType.TITLE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .originalText(true)
                .sourceId(new ObjectId())
                .projectId(new ObjectId())
                .languageCode(languageCode)
                .content(content)
                .build();

    }


}
