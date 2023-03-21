package de.dreipc.xcurator.xcuratorimportservice.models;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.rabbitmq.ProtoUtil;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.*;
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
@Data
@Document("xcurator-museum-objects")
@AllArgsConstructor
@Persistent
public class MuseumObject implements IdentifiedObject, ProtoModel, TimeStamp {
    @Id
    ObjectId id;
    @NonNull
    String externalId;
    @NonNull
    Location location;
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
    @Builder.Default
    List<String> keywords = new ArrayList<>();
    @Builder.Default
    List<String> materials = new ArrayList<>();
    @Builder.Default
    List<String> techniques = new ArrayList<>();
    @Builder.Default
    List<NamedEntity> namedEntities = new ArrayList<>();
    @NonNull
    DateRange dateRange;
    @Builder.Default
    List<ObjectId> assetIds = new ArrayList<>();
    @Builder.Default
    List<ObjectId> moduleIds = new ArrayList<>();
    @NonNull
    ObjectId projectId;
    @NonNull
    DataSource dataSource;

    @Tolerate
    public MuseumObject() {
    }

    @Tolerate
    public MuseumObject(ObjectId id) {
        this.id = id;
    }


    @Override
    public Message toProto() {
        var builder = XCuratorProtos.MuseumObjectProto.newBuilder()
                .setId(id.toString())
                .setExternalId(externalId)
                .setLocation((XCuratorProtos.LocationProto) location.toProto())
                .setCreatedAt(ProtoUtil.toProto(this.createdAt))
                .setUpdatedAt(ProtoUtil.toProto(this.updatedAt));
        builder.addAllMaterials(materials.stream().toList());
        builder.addAllKeywords(keywords.stream().toList());
        return builder.build();
    }

    public Message toProto(List<TextContent> textContentProtos) {
        var builder = XCuratorProtos.MuseumObjectProto.newBuilder()
                .setId(id.toString())
                .setExternalId(externalId)
                .setLocation((XCuratorProtos.LocationProto) location.toProto())
                .setCreatedAt(ProtoUtil.toProto(this.createdAt))
                .setDateRange((XCuratorProtos.DateRangeProto) dateRange.toProto())
                .setUpdatedAt(ProtoUtil.toProto(this.updatedAt));

        textContentProtos.forEach(textContent -> {
            if (textContent.getTextType() == TextType.TITLE) {
                builder.addTitles(textContent.toTextContentProto());
            } else if (textContent.getTextType() == TextType.DESCRIPTION) {
                builder.addDescriptions(textContent.toTextContentProto());
            }

        });


        builder.addAllMaterials(materials.stream().toList());
        builder.addAllKeywords(keywords.stream().toList());
        return builder.build();
    }


}
