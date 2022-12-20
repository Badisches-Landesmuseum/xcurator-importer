package de.dreipc.xcurator.xcuratorimportservice.models;

import org.bson.types.ObjectId;

public interface IdentifiedObject {

    ObjectId getId();

    void setId(ObjectId id);
}
