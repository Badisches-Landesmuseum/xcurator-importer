package de.dreipc.xcurator.xcuratorimportservice.utils;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoUtil {


    public static Query findAllIdsByQuerybuilder(ObjectId relatedId, String fieldName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(fieldName).is(relatedId));
        query.fields().include("_id");
        return query;
    }

}
