package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import de.dreipc.xcurator.xcuratorimportservice.repositories.StoryRepository;
import dreipc.graphql.types.CreateStory;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class StoreStoryCommand {


    private final StoryRepository repository;

    private final StoreModuleCommand storeModuleCommand;

    public StoreStoryCommand(StoryRepository repository, StoreModuleCommand storeModuleCommand) {
        this.repository = repository;
        this.storeModuleCommand = storeModuleCommand;
    }

    @Transactional
    public Story execute(CreateStory createStory) {
        var storyId = new ObjectId();
        var story = repository.save(Story.builder()
                .id(storyId)
                .topics(createStory.getTopics() != null ? createStory.getTopics() : Collections.emptyList())
                .build());

        var museumIds = createStory.getMuseumObjectIds().stream().map(ObjectId::new).toList();
        storeModuleCommand.execute(museumIds, storyId);

        return story;
    }
}
