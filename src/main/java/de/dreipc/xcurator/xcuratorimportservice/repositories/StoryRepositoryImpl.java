package de.dreipc.xcurator.xcuratorimportservice.repositories;

import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import dreipc.common.graphql.query.mongodb.GraphQlMongoDBEngine;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.StoryOrderByInput;
import dreipc.graphql.types.StoryWhereInput;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class StoryRepositoryImpl implements StoryRepositoryCustom {

    private final MongoTemplate template;
    private final GraphQlMongoDBEngine mongoDBEngine;

    public StoryRepositoryImpl(MongoTemplate template, GraphQlMongoDBEngine mongoDBEngine) {
        this.template = template;
        this.mongoDBEngine = mongoDBEngine;
    }

    @Override
    public CountConnection<Story> searchStories(int first, int skip, StoryWhereInput where, List<StoryOrderByInput> orderBy, DataFetchingEnvironment environment) {
        Query query = mongoDBEngine.buildQuery(first, skip, where, orderBy, environment, Story.class);

        var searchResult = template.find(query, Story.class);
        var totalCount = template.count(Query.of(query).limit(-1).skip(-1), Story.class);

        return new CountConnection<>(searchResult, totalCount, environment);
    }


}
