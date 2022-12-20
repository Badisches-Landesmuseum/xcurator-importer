package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.repositories.ModuleRepository;
import org.bson.types.ObjectId;

import java.util.List;


public class TestModuleRepositoryStub extends RepositoryStub<Module> implements ModuleRepository {


    @Override
    public List<Module> findAllByStoryId(ObjectId storyId) {
        return this.memoryStorage.values().stream().filter(
                object -> object.getStoryId().equals(storyId)).toList();
    }

    @Override
    public List<ObjectId> findAllIdsByStoryId(ObjectId storyId) {
        return findAllByStoryId(storyId)
                .stream()
                .map(Module::getId)
                .toList();
    }
}
