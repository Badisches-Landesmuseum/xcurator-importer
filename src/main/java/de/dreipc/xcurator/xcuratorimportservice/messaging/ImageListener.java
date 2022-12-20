package de.dreipc.xcurator.xcuratorimportservice.messaging;


import de.dreipc.xcurator.xcuratorimportservice.commands.DeleteMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import dreipc.q8r.proto.asset.AssetProtos;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ImageListener {

    private final AssetServiceProperties assetServiceProperties;

    private final MuseumObjectRepository museumObjectRepository;

    private final DeleteMuseumObjectCommand deleteMuseumObjectCommand;

    public ImageListener(AssetServiceProperties assetServiceProperties, MuseumObjectRepository museumObjectRepository, DeleteMuseumObjectCommand deleteMuseumObjectCommand) {
        this.assetServiceProperties = assetServiceProperties;
        this.museumObjectRepository = museumObjectRepository;
        this.deleteMuseumObjectCommand = deleteMuseumObjectCommand;
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "xcurator.image.create.error", durable = "true",
            arguments = @Argument(name = "x-dead-letter-exchange", value = "assets-dlx")),
            exchange = @Exchange(value = "assets", type = "topic"), key = "asset.image.store.error"),
            containerFactory = "rabbitListenerContainerFactory")
    private void failed(AssetProtos.ImageProto imageProto) {

        var museumObjectsIds = new ArrayList<ObjectId>();
        if(imageProto.getProjectId().equals(assetServiceProperties.getProjectId().toString())){
            try {
                var museumObjectId = new ObjectId(imageProto.getExternalIdsMap().get("xcurator-museum-object-id"));
                museumObjectsIds.add(museumObjectId);
            } catch (Exception e) {
                log.error("Can't delete museum object", e);
            }
        }
        deleteMuseumObjectCommand.executeByBulk(museumObjectsIds);

    }
}
