package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.Story;
import dreipc.common.graphql.relay.CountConnection;
import dreipc.graphql.types.StoryOrderByInput;
import dreipc.graphql.types.StoryWhereInput;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public interface StoryRepositoryCustom {

    CountConnection<Story> searchStories(int first, int skip, StoryWhereInput where, List<StoryOrderByInput> orderBy, DataFetchingEnvironment environment);
}
