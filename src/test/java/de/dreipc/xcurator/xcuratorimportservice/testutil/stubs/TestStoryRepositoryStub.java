package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import de.dreipc.xcurator.xcuratorimportservice.repositories.StoryRepository;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.StoryOrderByInput;
import dreipc.graphql.types.StoryWhereInput;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;


public class TestStoryRepositoryStub extends RepositoryStub<Story> implements StoryRepository {


    @Override
    public CountConnection<Story> searchStories(int first, int skip, StoryWhereInput where, List<StoryOrderByInput> orderBy, DataFetchingEnvironment environment) {
        return null;
    }
}
