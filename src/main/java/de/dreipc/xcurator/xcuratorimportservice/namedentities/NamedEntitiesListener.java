package de.dreipc.xcurator.xcuratorimportservice.namedentities;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import dreipc.common.graphql.exception.NotFoundException;
import dreipc.q8r.proto.asset.document.NamedEntitiesProtos;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NamedEntitiesListener {

    private final NamedEntityRepository namedEntityRepository;
    private final NamedEntityParser namedEntityParser;
    private final ProtoPublisher protoPublisher;
    private final AssetServiceProperties properties;
    private final TextContentRepository textContentRepository;


    public NamedEntitiesListener(NamedEntityParser namedEntityParser, ProtoPublisher protoPublisher, AssetServiceProperties properties, NamedEntityRepository namedEntityRepository, TextContentRepository textContentRepository) {
        this.namedEntityParser = namedEntityParser;
        this.protoPublisher = protoPublisher;
        this.properties = properties;
        this.namedEntityRepository = namedEntityRepository;
        this.textContentRepository = textContentRepository;

    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "xcurator.importer.entities.create",
            durable = "true", arguments = @Argument(name = "x-dead-letter-exchange", value = "assets-dlx")),
            exchange = @Exchange(value = "assets", type = "topic"), key = "entities.text.analysed"))
    public void addEntities(List<NamedEntitiesProtos.NamedEntitiesResultEventProto> protoList) {

        List<NamedEntity> entitiesToSave = new ArrayList<>();
        List<NamedEntitiesProtos.NamedEntitiesSavedEventProto> protos = new ArrayList<>();
        protoList.forEach(eventProto -> {
            try {

                var textContent = textContentRepository.findById(new ObjectId(eventProto.getSourceId())).orElseThrow(
                        () -> new NotFoundException("No text content with id: " + eventProto.getSourceId()));

                List<NamedEntity> entities = fromProtos(eventProto.getEntitiesList(), textContent.getSourceId(), properties.getProjectId());
                var savedProto = NamedEntitiesProtos.NamedEntitiesSavedEventProto.newBuilder()
                        .setId(textContent.getSourceId().toString())
                        .addAllEntities(toProtos(entities))
                        .build();

                entitiesToSave.addAll(entities);
                protos.add(savedProto);

            } catch (
                    Exception e) {
                log.error("Did not saved entities");
            }
        });

        namedEntityRepository.saveAll(entitiesToSave);
        protos.forEach(this::publish);
    }

    private void publish(NamedEntitiesProtos.NamedEntitiesSavedEventProto proto) {
        protoPublisher.sendEvent("xcurator.entities.saved", proto);
    }

    private List<NamedEntitiesProtos.NamedEntityProto> toProtos(List<NamedEntity> namedEntities) {
        return namedEntities.stream().map(entity -> (NamedEntitiesProtos.NamedEntityProto) entity.toProto()).toList();
    }

    private List<NamedEntity> fromProtos(List<NamedEntitiesProtos.NamedEntityProto> namedEntityProtos, ObjectId museumObjectId, ObjectId projectId) {
        return namedEntityProtos.stream().map(namedEntityProto -> namedEntityParser.parse(namedEntityProto, museumObjectId, projectId)).toList();
    }


}
