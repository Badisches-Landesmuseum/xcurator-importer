package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SyncArtifactCommand {

    private final MuseumObjectRepository museumObjectRepository;

    private final ProtoPublisher publisher;
    private final TextContentRepository textContentRepository;

    private final NamedEntityRepository namedEntityRepository;


    public SyncArtifactCommand(MuseumObjectRepository museumObjectRepository, ProtoPublisher publisher, TextContentRepository textContentRepository, NamedEntityRepository namedEntityRepository) {
        this.museumObjectRepository = museumObjectRepository;
        this.publisher = publisher;
        this.textContentRepository = textContentRepository;
        this.namedEntityRepository = namedEntityRepository;
    }

    public int execute() {
        var ids = museumObjectRepository.findAllIds().stream().toList();
        log.info("Starting sync command of: " + ids.size() + " artifacts");
        this.publishMuseumObject(ids);
        return ids.size();
    }

    private void publishMuseumObject(List<ObjectId> artifactIds) {
        var artifacts = museumObjectRepository.findAllById(artifactIds);
        artifacts.forEach(this::publish);
    }

    public void publish(MuseumObject artifact) {
        var message = artifact.toProto(textContentRepository.findAllBySourceId(artifact.getId()));
        publisher.sendEvent("xcurator.museumobject.synced", message);

        // TODO: Add named entities
        // TODO: Add topics

    }

}
