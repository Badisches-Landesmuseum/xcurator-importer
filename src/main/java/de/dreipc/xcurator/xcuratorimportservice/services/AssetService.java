package de.dreipc.xcurator.xcuratorimportservice.services;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.rabbitmq.ProtoRPCHandlerAsync;
import de.dreipc.rabbitmq.ProtoUtil;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumImage;
import dreipc.q8r.proto.asset.AssetProtos;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AssetService {

    private final ProtoPublisher publisher;
    private final AssetServiceProperties properties;

    public AssetService(AssetServiceProperties properties, ProtoPublisher publisher) {
        this.publisher = publisher;
        this.properties = properties;

        if (properties.getProjectId() == null)
            throw new IllegalArgumentException("projectId is required but not given. Check the application.yml please!");
    }

    public AssetServiceProperties getProperties() {
        return properties;
    }


    public void store(AssetProtos.ImageProto imageProto) {
        publisher.sendEvent("asset.image.store", imageProto);
    }

    public void store(Iterable<MuseumImage> images) {
        AtomicInteger counter = new AtomicInteger();

        StreamSupport.stream(images.spliterator(), false)

                .map(MuseumImage::toProto).toList().forEach(imageProto -> {
                    store(imageProto);
                    counter.getAndIncrement();
                });

    }

    public void initImportSequence(String replyQueue, String correlationId) {

        LocalDate localDateTime = LocalDate.parse("2022-01-01");
        Instant from = localDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        var action = AssetProtos.AssetStoreInitActionProto
                .newBuilder()
                .setId(correlationId)
                .setProjectId(this.getProperties().getProjectId().toString())
                .setFrom(ProtoUtil.toProto(from))
                .setUntil(ProtoUtil.toProto(Instant.now()))
                .addType(AssetProtos.AssetTypeProto.IMAGE)
                .build();


        publisher.sendEvent("asset.asset.import.init", action, correlationId, replyQueue);
        log.info("Initiated import sequence for project: " + this.properties.getProjectId());
        log.info("Waiting for asset service to delete images");
    }

    public void delete(ObjectId assetId) {
        if (assetId == null) return;
        publisher.sendEvent("asset.delete", AssetProtos.DeleteAssetAction.newBuilder()
                .setId(assetId.toString())
                .setProjectId(getProperties().getProjectId().toString())
                .build());
    }

    public void delete(List<ObjectId> assetIds) {
        assetIds.forEach(this::delete);
    }


}
