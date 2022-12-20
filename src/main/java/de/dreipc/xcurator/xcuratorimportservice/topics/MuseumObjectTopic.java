package de.dreipc.xcurator.xcuratorimportservice.topics;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.xcurator.xcuratorimportservice.models.IdentifiedObject;
import de.dreipc.xcurator.xcuratorimportservice.models.TimeStamp;
import dreipc.xcurator.proto.XCuratorProtos;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Document("xcurator-topics")
@Data
@Persistent
public class MuseumObjectTopic implements ProtoModel, TimeStamp, IdentifiedObject {

    @Id
    ObjectId id;
    @NonNull
    @Indexed
    ObjectId sourceId;
    @NonNull ObjectId projectId;
    @Builder.Default
    List<String> labels = new ArrayList<>();
    float weight;

    @CreatedDate
    @Getter
    @Indexed
    @NonNull Instant createdAt;
    @LastModifiedDate
    @Getter
    @Indexed
    @NonNull Instant updatedAt;

    @Tolerate
    public MuseumObjectTopic() {
    }

    @Override
    public Message toProto() {
        return XCuratorProtos.TopicProto.newBuilder()
                .setId(id.toString())
                .setSourceId(sourceId.toString())
                .addAllLabels(labels)
                .setWeight(weight)
                .build();
    }
}
