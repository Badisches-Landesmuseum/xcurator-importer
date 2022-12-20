package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.repositories.TopicRepository;
import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class TestTopicRepositoryStub extends RepositoryStub<MuseumObjectTopic> implements TopicRepository {

    @Override
    public List<MuseumObjectTopic> findAllBySourceId(ObjectId sourceId) {
        return this.memoryStorage.values()
                .stream().filter(object -> object.getSourceId()
                        .equals(sourceId)).toList();
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds) {

        return sourceIds.stream().map(this::findAllBySourceId)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .map(MuseumObjectTopic::getId)
                .toList();
    }

    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        this.memoryStorage.values().stream()
                .filter(object -> museumObjectIds.contains(object.getSourceId()))
                .map(MuseumObjectTopic::getId)
                .map(id -> memoryStorage.remove(id));
    }


}
