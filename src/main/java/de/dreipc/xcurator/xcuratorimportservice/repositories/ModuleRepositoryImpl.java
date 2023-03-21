package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import de.dreipc.xcurator.xcuratorimportservice.utils.MongoUtil;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class ModuleRepositoryImpl implements ModuleRepositoryCustom {

    private final MongoTemplate template;

    public ModuleRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }


    @Override
    public List<ObjectId> findAllIdsByStoryId(ObjectId storyId) {
        var query = MongoUtil.findAllIdsByQuerybuilder(storyId, "storyId");
        var results = template.find(query, Module.class);
        return results.stream().map(Module::getId).toList();
    }
}
