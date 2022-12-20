package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class NamedEntityRepositoryImpl implements NamedEntityRepositoryCustom {

    private final MongoTemplate template;

    public NamedEntityRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").in(sourceIds));
        query.fields().include("_id");
        var results = template.find(query, NamedEntity.class);
        return results.stream().map(NamedEntity::getId).toList();
    }

    @Override
    public void deleteAllByMuseumObjectIds(List<ObjectId> museumObjectIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("museumObjectId").in(museumObjectIds));
        template.remove(query, NamedEntity.class);
    }


}
