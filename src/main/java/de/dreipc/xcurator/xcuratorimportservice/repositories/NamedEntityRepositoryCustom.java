package de.dreipc.xcurator.xcuratorimportservice.repositories;


import org.bson.types.ObjectId;

import java.util.List;

public interface NamedEntityRepositoryCustom {

    List<ObjectId> findAllExistedSourceIds(List<ObjectId> sourceIds);

    void deleteAllByMuseumObjectIds(List<ObjectId> museumObjectId);


}
