package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NamedEntityRepository extends MongoRepository<NamedEntity, ObjectId>, NamedEntityRepositoryCustom {

    List<NamedEntity> findAllBySourceId(ObjectId sourceId);

    void deleteAllBySourceId(List<ObjectId> sourceId);

}
