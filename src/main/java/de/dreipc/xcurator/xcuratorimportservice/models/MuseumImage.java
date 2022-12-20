package de.dreipc.xcurator.xcuratorimportservice.models;

import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.rabbitmq.ProtoUtil;
import dreipc.q8r.proto.asset.AssetProtos;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.time.Instant;

@Data
@Builder
@Document("xcurator-images")
@Persistent
public class MuseumImage implements TimeStamp, ProtoModel {

    @Id
    @NonNull
    ObjectId id;
    @NonNull
    ObjectId sourceId;
    @NonNull
    @NonFinal
    @Builder.Default
    String fileName = "";
    @NonNull
    @Builder.Default
    String title = "";
    @NotNull
    URL sourceUrl;
    @NonNull
    Instant createdAt;
    @NonNull
    Instant updatedAt;
    @NonNull
    ObjectId projectId;


    public AssetProtos.ImageProto toProto() {


        var file = AssetProtos.AssetFileProto.newBuilder()
                .setUrl(this.getSourceUrl().toString())
                .setName(this.getFileName()).build();

        var imageProto = AssetProtos.ImageProto.newBuilder()
                .setId(id.toString())
                .setProjectId(this.getProjectId().toString())
                .setFile(file);

        imageProto.setCreatedAt(ProtoUtil.toProto(Instant.now()));
        imageProto.setUpdatedAt(ProtoUtil.toProto(Instant.now()));
        imageProto.putExternalIds("xcurator-museum-object-id", this.getSourceId().toString());

        return imageProto.build();
    }

}
