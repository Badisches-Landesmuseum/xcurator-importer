package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import org.bson.types.ObjectId;

import java.util.List;


public class TestTopicRepositoryStub extends RepositoryStub<MuseumObjectTopic> implements TopicRepository {

    @Override
    public List<MuseumObjectTopic> findAllBySourceId(ObjectId sourceId) {
        return this.memoryStorage.values()
                .stream().filter(object -> object.getSourceId()
                        .equals(sourceId)).toList();
    }


    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        this.memoryStorage.values().stream()
                .filter(object -> museumObjectIds.contains(object.getSourceId()))
                .map(MuseumObjectTopic::getId)
                .map(memoryStorage::remove);
    }


}
