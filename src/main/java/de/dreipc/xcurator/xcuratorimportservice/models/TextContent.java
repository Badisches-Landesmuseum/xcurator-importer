package de.dreipc.xcurator.xcuratorimportservice.models;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.*;
import lombok.experimental.Tolerate;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@Document("xcurator-text-content")
public class TextContent implements ProtoModel, TimeStamp, IdentifiedObject {

    @Id
    @Generated
    ObjectId id;
    @NonNull
    @Builder.Default
    String content = "";
    @Indexed
    @NonNull
    Boolean originalText;
    @NonNull
    @Indexed
    LanguageCode languageCode;
    @NonNull
    @Indexed
    ObjectId sourceId;
    @NonNull
    ObjectId projectId;
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
    @NonNull
    @Indexed
    TextType textType;

    @Tolerate
    public TextContent() {
    }

    @Override
    public Message toProto() {
        var proto = XCuratorProtos.TextContentProto.newBuilder()
                .setId(id.toString())
                .setContent(content)
                .setOriginalText(originalText)
                .setLanguageCode(languageCode.toString());

        return proto.build();
    }

    public XCuratorProtos.TextContentProto toTextContentProto() {
        return (XCuratorProtos.TextContentProto) this.toProto();
    }

}
