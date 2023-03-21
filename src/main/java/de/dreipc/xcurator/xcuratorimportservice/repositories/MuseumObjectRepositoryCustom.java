package de.dreipc.xcurator.xcuratorimportservice.repositories;


import org.bson.types.ObjectId;

import java.util.List;

public interface MuseumObjectRepositoryCustom {
    List<ObjectId> findAllIds();

}
