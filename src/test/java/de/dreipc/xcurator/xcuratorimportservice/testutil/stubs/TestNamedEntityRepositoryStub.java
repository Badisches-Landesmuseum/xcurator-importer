package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TestNamedEntityRepositoryStub extends RepositoryStub<NamedEntity> implements NamedEntityRepository {

    @Override
    public List<NamedEntity> findAllBySourceId(ObjectId sourceId) {
        return this.memoryStorage.values()
                .stream().filter(object -> object.getSourceId()
                        .equals(sourceId)).toList();
    }

    @Override
    public void deleteAllBySourceId(List<ObjectId> sourceIDS) {
        this.memoryStorage.entrySet().stream()
                .filter(entryset -> sourceIDS.contains(entryset.getValue().getSourceId()))
                .map(Map.Entry::getKey)
                .map(this.memoryStorage::remove);
    }


    @Override
    public List<ObjectId> findAllExistedSourceIds(List<ObjectId> sourceIds) {

        return sourceIds.stream().map(this::findAllBySourceId)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .map(NamedEntity::getId)
                .toList();
    }

    @Override
    public void deleteAllByMuseumObjectIds(List<ObjectId> museumObjectIds) {
        this.memoryStorage.values().stream()
                .filter(object -> museumObjectIds.contains(object.getMuseumObjectId()))
                .map(NamedEntity::getId)
                .map(memoryStorage::remove);
    }


}
