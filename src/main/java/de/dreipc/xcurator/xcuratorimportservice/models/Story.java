package de.dreipc.xcurator.xcuratorimportservice.models;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document("xcurator-stories")
@Persistent
public class Story implements IdentifiedObject {

    @Id
    ObjectId id;

    @Builder.Default
    List<String> topics = new ArrayList<>();

}
