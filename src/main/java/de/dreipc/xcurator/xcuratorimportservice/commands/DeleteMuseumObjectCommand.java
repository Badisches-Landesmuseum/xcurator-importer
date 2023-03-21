package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeleteMuseumObjectCommand {

    private final MuseumObjectRepository museumObjectRepository;
    private final TextContentRepository textContentRepository;
    private final NamedEntityRepository namedEntityRepository;
    private final TopicRepository topicRepository;
    private final ProtoPublisher announce;


    public DeleteMuseumObjectCommand(MuseumObjectRepository museumObjectRepository, TextContentRepository textContentRepository, NamedEntityRepository namedEntityRepository, TopicRepository topicRepository, ProtoPublisher publisher) {
        this.museumObjectRepository = museumObjectRepository;
        this.textContentRepository = textContentRepository;
        this.namedEntityRepository = namedEntityRepository;
        this.topicRepository = topicRepository;
        this.announce = publisher;
    }

    public MuseumObject executeById(ObjectId id) {
        executeByBulk(List.of(id));
        return new MuseumObject(id);
    }

    public List<MuseumObject> deleteAll() {
        return executeByBulk(museumObjectRepository.findAllIds());
    }

    public List<MuseumObject> executeByBulk(@NonNull List<ObjectId> ids) {
        museumObjectRepository.deleteAllById(ids);
        textContentRepository.deleteAllBySourceId(ids);
        namedEntityRepository.deleteAllByMuseumObjectIds(ids);
        topicRepository.deleteAllBySourceId(ids);
        ids.forEach(this::publish);
        log.info("Deleted: " + ids.size() + " artifacts");
        return ids.stream().map(MuseumObject::new).toList();
    }


    private void publish(ObjectId id) {
        var deletedProto = XCuratorProtos.MuseumObjectDeletedEvent
                .newBuilder()
                .setId(id.toString())
                .build();
        announce.sendEvent("xcurator.museumobject.deleted", deletedProto);

    }
}
