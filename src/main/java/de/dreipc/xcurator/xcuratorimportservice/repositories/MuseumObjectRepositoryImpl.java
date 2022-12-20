package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import dreipc.common.graphql.query.mongodb.GraphQlMongoDBEngine;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.List;

public class MuseumObjectRepositoryImpl implements MuseumObjectRepositoryCustom {

    private final MongoTemplate template;
    private final GraphQlMongoDBEngine mongoDBEngine;

    public MuseumObjectRepositoryImpl(MongoTemplate template, GraphQlMongoDBEngine mongoDBEngine) {
        this.template = template;
        this.mongoDBEngine = mongoDBEngine;
    }


    @Override
    public List<ObjectId> findAllIds() {
        Query query = new Query();
        query.fields().include("_id");
        var results = template.find(query, MuseumObject.class);
        return results.stream().map(MuseumObject::getId).toList();
    }


    @Override
    public void addAssetId(ObjectId id, ObjectId assetId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        UpdateDefinition updateDefinition = new Update().push("assetIds", assetId);
        template.updateMulti(query, updateDefinition, MuseumObject.class);
    }


}
