package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MuseumObjectRepositoryImpl implements MuseumObjectRepositoryCustom {

    private final MongoTemplate template;

    public MuseumObjectRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }


    @Override
    public List<ObjectId> findAllIds() {
        Query query = new Query();
        query.fields().include("_id");
        var results = template.find(query, MuseumObject.class);
        return results.stream().map(MuseumObject::getId).toList();
    }


}
