package de.dreipc.xcurator.xcuratorimportservice.topics;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import dreipc.common.graphql.exception.NotFoundException;
import dreipc.q8r.proto.asset.document.TopicDetectionProtos;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TopicsListener {

    private final TopicRepository topicRepository;
    private final ProtoPublisher protoPublisher;

    private final MuseumObjectRepository museumObjectRepository;
    private final AssetServiceProperties properties;


    public TopicsListener(TopicRepository topicRepository, ProtoPublisher protoPublisher, MuseumObjectRepository museumObjectRepository, AssetServiceProperties properties) {
        this.topicRepository = topicRepository;
        this.protoPublisher = protoPublisher;
        this.museumObjectRepository = museumObjectRepository;
        this.properties = properties;

    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "xcurator.importer.topics.create", durable = "true", arguments = @Argument(name = "x-dead-letter-exchange", value = "assets-dlx")),
            exchange = @Exchange(value = "assets", type = "topic"), key = "xcurator.topics.created"))
    public void addTopics(List<TopicDetectionProtos.TopicDetectionActionProto> protoList) {

        List<MuseumObjectTopic> topicsToSave = new ArrayList<>();


        protoList.forEach(eventProto -> {
            try {
                var museumObjectId = new ObjectId(eventProto.getDocumentId());
                var museumObjectExists = museumObjectRepository.existsById(museumObjectId);
                if (!museumObjectExists)
                    throw new NotFoundException("No Museum object with id: " + museumObjectId);

                var topics = this.fromProtos(eventProto.getTopicList(), museumObjectId, properties.getProjectId());

                topicsToSave.addAll(topics);

            } catch (Exception e) {
                log.error("Did not save topics", e);
            }
        });

        var saved = topicRepository.saveAll(topicsToSave);

        saved.stream().map(MuseumObjectTopic::toProto)
                .map(topic -> (XCuratorProtos.TopicProto) topic)
                .forEach(this::publish);
    }

    private void publish(XCuratorProtos.TopicProto proto) {
        protoPublisher.sendEvent("xcurator.topics.saved", proto);
    }

    private List<MuseumObjectTopic> fromProtos(List<TopicDetectionProtos.TopicProto> topicProtos, ObjectId museumObjectId, ObjectId projectId) {

        return topicProtos.stream().map(topicProto -> MuseumObjectTopic.builder()
                .id(new ObjectId())
                .sourceId(museumObjectId)
                .labels(getLabels(topicProto.getTopicKeyWordsList()))
                .weight(topicProto.getWeight()).projectId(projectId)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build()).toList();

    }


    private List<String> getLabels(List<TopicDetectionProtos.TopicKeyWordProto> list) {

        return list.stream().map(TopicDetectionProtos.TopicKeyWordProto::getLabel).toList();
    }


}
