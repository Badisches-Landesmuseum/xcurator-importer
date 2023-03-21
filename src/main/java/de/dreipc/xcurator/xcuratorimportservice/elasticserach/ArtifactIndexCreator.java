package de.dreipc.xcurator.xcuratorimportservice.elasticserach;

import com.google.common.collect.Lists;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class ArtifactIndexCreator {

    private final ArtifactIndexRepository repository;

    private final TopicRepository topicRepository;

    private final MuseumObjectRepository museumObjectRepository;


    private final TextContentRepository textContentRepository;

    private final int BATCH_SIZE = 100;

    public ArtifactIndexCreator(ArtifactIndexRepository repository, TopicRepository topicRepository, MuseumObjectRepository museumObjectRepository, TextContentRepository textContentRepository) {
        this.repository = repository;
        this.topicRepository = topicRepository;
        this.museumObjectRepository = museumObjectRepository;
        this.textContentRepository = textContentRepository;
    }


    public int execute() {
        log.info("Start creating artifact indexes");
        var batch = Lists.partition(museumObjectRepository.findAllIds(), BATCH_SIZE);
        var total = batch.stream().map(this::execute).reduce(0, Integer::sum);
        log.info("Saved: " + total + " artifacts");
        return total;
    }

    public int execute(List<ObjectId> artifactIds) {
        try {
            var indexes = StreamSupport.stream(museumObjectRepository.findAllById(artifactIds).spliterator(), false).map(this::artifactIndexBuilder).toList();
            repository.saveAll(indexes);
            return indexes.size();
        } catch (Exception e) {
            log.error("Did not perform batch saving", e);
            return 0;
        }

    }

    private ArtifactIndex artifactIndexBuilder(MuseumObject artifact) {
        try {

            return ArtifactIndex
                    .builder()
                    .id(artifact.getId().toString())
                    .locationName(artifact.getLocation().getName())
                    .countryName(artifact.getLocation().getCountryName())
                    .createdAt(artifact.getCreatedAt())
                    .updatedAt(artifact.getUpdatedAt())
                    .keywords(artifact.getKeywords())
                    .materials(artifact.getMaterials())
                    .titles(flattenTexts(artifact.getId(), TextType.TITLE))
                    .descriptions(flattenTexts(artifact.getId(), TextType.DESCRIPTION))
                    .topics(flattenTopics(artifact.getId()))
                    .epoch(artifact.getDateRange().getEpoch())
                    .dataSource(artifact.getDataSource().toString())
                    .build();
        } catch (Exception e) {
            log.error("cant parse object", e);
            return null;
        }
    }

    public List<String> flattenTexts(ObjectId artifactId, TextType textType) {
        try {
            return textContentRepository.findTextContentBySourceIdAndTextType(artifactId, textType)
                    .stream()
                    .map(TextContent::getContent)
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }


    }

    public List<String> flattenTopics(ObjectId artifactId) {

        return topicRepository.findAllBySourceId(artifactId)
                .stream()
                .sorted(Comparator.comparing(MuseumObjectTopic::getWeight))
                .findFirst()
                .get()
                .getLabels();


    }

}
