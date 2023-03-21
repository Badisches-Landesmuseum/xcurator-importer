package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MuseumObjectRepository extends MongoRepository<MuseumObject, ObjectId>, MuseumObjectRepositoryCustom {

    boolean existsByExternalId(String externalId);

}
