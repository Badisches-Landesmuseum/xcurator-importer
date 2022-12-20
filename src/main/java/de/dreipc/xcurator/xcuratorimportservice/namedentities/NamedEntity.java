package de.dreipc.xcurator.xcuratorimportservice.namedentities;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.xcurator.xcuratorimportservice.models.IdentifiedObject;
import de.dreipc.xcurator.xcuratorimportservice.models.TimeStamp;
import dreipc.q8r.proto.asset.document.NamedEntitiesProtos;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.time.Instant;

@Builder
@Document("xcurator-named-entities")
@Data
@Persistent
public class NamedEntity implements ProtoModel, TimeStamp, IdentifiedObject {

    @Id
    ObjectId id;
    @NonNull
    @Indexed
    ObjectId sourceId;
    @NonNull
    ObjectId projectId;
    @NonNull
    String type;
    @NonNull
    String literal;
    @NonNull
    Integer startPosition;
    @NonNull
    Integer endPosition;
    @CreatedDate
    @Getter
    @Indexed
    @NonNull
    Instant createdAt;
    @LastModifiedDate
    @Getter
    @Indexed
    @NonNull
    Instant updatedAt;
    String knowledgeBaseId;
    URL knowledgeBaseUrl;
    @NonNull
    @Indexed
    ObjectId museumObjectId;

    @Tolerate
    public NamedEntity() {
    }

    @Override
    public Message toProto() {
        return NamedEntitiesProtos.NamedEntityProto.newBuilder()
                .setId(id.toString())
                .setSourceId(sourceId.toString())
                .setType(type)
                .setLiteral(literal)
                .setStartPosition(startPosition)
                .setEndPosition(endPosition)
                .setProjectId(projectId.toString())
                .build();
    }
}
