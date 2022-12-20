package de.dreipc.xcurator.xcuratorimportservice.repositories;


import org.bson.types.ObjectId;

import java.util.List;

public interface TopicRepositoryCustom {

    List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds);

    void deleteAllBySourceId(List<ObjectId> museumObjectIds);

}
