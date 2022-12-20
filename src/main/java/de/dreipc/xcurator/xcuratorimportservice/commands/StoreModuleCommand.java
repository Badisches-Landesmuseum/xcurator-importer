package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.repositories.ModuleRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.StoryRepository;
import dreipc.common.graphql.exception.NotFoundException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreModuleCommand {

    private final ModuleRepository repository;
    private final MuseumObjectRepository museumObjectRepository;

    private final StoryRepository storyRepository;

    public StoreModuleCommand(ModuleRepository repository, MuseumObjectRepository museumObjectRepository, StoryRepository storyRepository) {
        this.repository = repository;
        this.museumObjectRepository = museumObjectRepository;
        this.storyRepository = storyRepository;
    }

    public Module execute(ObjectId museumObjectId, ObjectId storyId) {
        var museumObjectExists = museumObjectRepository.existsById(museumObjectId);
        if (!museumObjectExists)
            throw new NotFoundException("Problem creating module, museum object id was not found: " + museumObjectId);

        var storyExists = storyRepository.existsById(storyId);
        if (!storyExists)
            new NotFoundException("Problem creating module, story id was not found: " + storyId);

        var module = Module.builder()
                .id(new ObjectId())
                .storyId(storyId)
                .museumObjectId(museumObjectId)
                .build();

        return repository.save(module);
    }


    public List<Module> execute(List<ObjectId> museumObjectIds, ObjectId storyId) {
        return museumObjectIds.stream().map(museumObjectId -> execute(museumObjectId, storyId)).toList();
    }
}
