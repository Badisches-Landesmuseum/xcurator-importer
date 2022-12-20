package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TopicRepository extends MongoRepository<MuseumObjectTopic, ObjectId>, TopicRepositoryCustom {

    List<MuseumObjectTopic> findAllBySourceId(ObjectId sourceId);


}
