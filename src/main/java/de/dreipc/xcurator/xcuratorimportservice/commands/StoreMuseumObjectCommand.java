package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumImage;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumResult;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntitiesRequester;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class StoreMuseumObjectCommand {
    private final ProtoPublisher publisher;
    private final MuseumObjectRepository repository;
    private final TextContentRepository textContentRepository;
    private final NamedEntitiesRequester namedEntitiesRequester;
    private final AssetService assetService;


    public StoreMuseumObjectCommand(ProtoPublisher publisher, MuseumObjectRepository museumObjectRepository, TextContentRepository textContentRepository, NamedEntitiesRequester namedEntitiesRequester, AssetService assetService) {
        this.publisher = publisher;
        this.repository = museumObjectRepository;
        this.textContentRepository = textContentRepository;
        this.namedEntitiesRequester = namedEntitiesRequester;
        this.assetService = assetService;
    }


    public List<MuseumObject> save(List<MuseumResult> museumResults) {
        var saved = this.saveMuseumResults(museumResults.stream().map(MuseumResult::getMuseumObject).toList());
        this.saveAllTextContent(museumResults.stream().map(MuseumResult::getTexts).toList().stream().flatMap(Collection::stream).toList());
        this.saveAllImages(museumResults.stream().map(MuseumResult::getImages).flatMap(Collection::stream).toList());
        museumResults.forEach(this::publish);
        return saved;
    }

    public List<MuseumObject> saveMuseumResults(List<MuseumObject> museumObjects) {
        var saved = this.repository.insert(museumObjects);
        return saved;
    }

    public List<TextContent> saveAllTextContent(List<TextContent> textContents) {
        var saved = this.textContentRepository.insert(textContents);
        namedEntitiesRequester.execute(saved);
        return saved;
    }

    public List<MuseumImage> saveAllImages(List<MuseumImage> museumImages) {
        assetService.store(museumImages);
        return museumImages;
    }

    public void publish(MuseumResult museumResult) {
        var museumObject = museumResult
                .getMuseumObject().
                toProto(museumResult.getTexts().stream()
                        .toList());
        publisher.sendEvent("xcurator.museumobject.created", museumObject);
    }

}
