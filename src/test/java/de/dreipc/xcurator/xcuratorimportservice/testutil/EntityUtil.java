package de.dreipc.xcurator.xcuratorimportservice.testutil;

import de.dreipc.xcurator.xcuratorimportservice.models.DateRange;
import de.dreipc.xcurator.xcuratorimportservice.models.Location;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
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
                .build();
    }


}
