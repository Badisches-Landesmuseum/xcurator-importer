package de.dreipc.xcurator.xcuratorimportservice.repositories;


import org.bson.types.ObjectId;

import java.util.List;

public interface ModuleRepositoryCustom {

    List<ObjectId> findAllIdsByStoryId(ObjectId storyId);

}
