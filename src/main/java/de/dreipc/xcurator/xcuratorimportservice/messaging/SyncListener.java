package de.dreipc.xcurator.xcuratorimportservice.messaging;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import dreipc.q8r.proto.sync.SyncProtos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncListener {

    public static final String EXCHANGE = "assets";

    private static final String ROUTING_KEY = "xcurator.museumobject.synced";

    private final MuseumObjectRepository museumObjectRepository;

    private final RabbitTemplate rabbitTemplate;

    public SyncListener(MuseumObjectRepository museumObjectRepository, RabbitTemplate rabbitTemplate, ProtoPublisher protoPublisher) {
        this.museumObjectRepository = museumObjectRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "xcurator.sync.create", durable = "true",
            arguments = @Argument(name = "x-dead-letter-exchange", value = "assets-dlx")),
            exchange = @Exchange(value = "assets", type = "topic"), key = "sync.started"))
    private void syncCreated(SyncProtos.SyncProto proto) {

        if (proto.getType() == SyncProtos.SyncTypeProto.XCURATOR) {
            museumObjectRepository.findAll().stream()
                    .map(MuseumObject::toProto)
                    .forEach(artifactProto -> rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, artifactProto, m -> {
                        m
                                .getMessageProperties()
                                .setCorrelationId(proto.getId());
                        return m;
                    }));
        } else {
            log.error("Following sync type is not yet supported");
        }

    }
}
