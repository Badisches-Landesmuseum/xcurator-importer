package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class TextContentRepositoryImpl implements TextContentRepositoryCustom {

    private final MongoTemplate template;

    public TextContentRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").in(sourceIds));
        query.fields().include("_id");
        var results = template.find(query, TextContent.class);
        return results.stream().map(TextContent::getId).toList();
    }

    @Override
    public List<TextContent> findAllBySourceId(ObjectId sourceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").is(sourceId));
        return template.find(query, TextContent.class);
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIdAndTextType(ObjectId sourceId, TextType textType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").is(sourceId));
        query.addCriteria(Criteria.where("textType").is(textType));
        query.fields().include("_id");
        var results = template.find(query, TextContent.class);
        return results.stream().map(TextContent::getId).toList();
    }

    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceId").in(museumObjectIds));
        template.remove(query, TextContent.class);
    }


}
