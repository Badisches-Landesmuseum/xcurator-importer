package de.dreipc.xcurator.xcuratorimportservice.models;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("xcurator-modules")
@Persistent
public class Module implements IdentifiedObject {

    @Id
    ObjectId id;
    @Indexed
    ObjectId museumObjectId;
    @Indexed
    ObjectId storyId;
}
