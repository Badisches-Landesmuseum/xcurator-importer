package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface MuseumObjectRepository extends MongoRepository<MuseumObject, ObjectId>, MuseumObjectRepositoryCustom {

    boolean existsByExternalId(String externalId);

}
