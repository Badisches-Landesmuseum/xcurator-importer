package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import org.bson.types.ObjectId;

import java.util.List;


public class TestMuseumObjectRepositoryStub extends RepositoryStub<MuseumObject> implements MuseumObjectRepository {

    @Override
    public List<ObjectId> findAllIds() {
        return this.memoryStorage
                .keySet()
                .stream()
                .toList();
    }


    @Override
    public void addAssetId(ObjectId id, ObjectId assetId) {
        this.memoryStorage
                .get(id)
                .getAssetIds()
                .add(assetId);
    }

    @Override
    public boolean existsByExternalId(String externalId) {
        return this.memoryStorage
                .values()
                .stream()
                .filter(elem -> elem
                        .getExternalId()
                        .equals(externalId))
                .findFirst()
                .isPresent();
    }
}
