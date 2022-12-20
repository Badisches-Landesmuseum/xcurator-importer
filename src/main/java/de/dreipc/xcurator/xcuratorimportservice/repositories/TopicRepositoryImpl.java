package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.topics.MuseumObjectTopic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class TopicRepositoryImpl implements TopicRepositoryCustom {

    private final MongoTemplate template;

    public TopicRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").in(sourceIds));
        query.fields().include("_id");
        var results = template.find(query, MuseumObjectTopic.class);
        return results.stream().map(MuseumObjectTopic::getId).toList();
    }

    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").in(museumObjectIds));
        template.remove(query, MuseumObjectTopic.class);
    }


}
