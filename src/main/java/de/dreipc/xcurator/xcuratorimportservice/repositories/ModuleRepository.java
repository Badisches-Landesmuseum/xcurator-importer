package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.Module;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ModuleRepository extends MongoRepository<Module, ObjectId>, ModuleRepositoryCustom {

    List<Module> findAllByStoryId(ObjectId id);

}
