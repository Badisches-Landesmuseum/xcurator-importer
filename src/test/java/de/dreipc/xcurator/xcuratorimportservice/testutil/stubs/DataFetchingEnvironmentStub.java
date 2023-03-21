package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import graphql.GraphQLContext;
import graphql.cachecontrol.CacheControl;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.MergedField;
import graphql.execution.directives.QueryDirectives;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.OperationDefinition;
import graphql.schema.*;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataFetchingEnvironmentStub implements DataFetchingEnvironment {
    @Override
    public <T> T getSource() {
        return null;
    }

    @Override
    public Map<String, Object> getArguments() {
        return null;
    }

    @Override
    public boolean containsArgument(String name) {
        return false;
    }

    @Override
    public <T> T getArgument(String name) {
        return null;
    }

    @Override
    public <T> T getArgumentOrDefault(String name, T defaultValue) {
        return null;
    }

    @Override
    public <T> T getContext() {
        return null;
    }

    @Override
    public GraphQLContext getGraphQlContext() {
        return null;
    }

    @Override
    public <T> T getLocalContext() {
        return null;
    }

    @Override
    public <T> T getRoot() {
        return null;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return null;
    }

    @Override
    public List<Field> getFields() {
        return null;
    }

    @Override
    public MergedField getMergedField() {
        return null;
    }

    @Override
    public Field getField() {
        return null;
    }

    @Override
    public GraphQLOutputType getFieldType() {
        return null;
    }

    @Override
    public ExecutionStepInfo getExecutionStepInfo() {
        return null;
    }

    @Override
    public GraphQLType getParentType() {
        return null;
    }

    @Override
    public GraphQLSchema getGraphQLSchema() {
        return null;
    }

    @Override
    public Map<String, FragmentDefinition> getFragmentsByName() {
        return null;
    }

    @Override
    public ExecutionId getExecutionId() {
        return null;
    }

    @Override
    public DataFetchingFieldSelectionSet getSelectionSet() {
        return null;
    }

    @Override
    public QueryDirectives getQueryDirectives() {
        return null;
    }

    @Override
    public <K, V> DataLoader<K, V> getDataLoader(String dataLoaderName) {
        return null;
    }

    @Override
    public DataLoaderRegistry getDataLoaderRegistry() {
        return null;
    }

    @Override
    public CacheControl getCacheControl() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public OperationDefinition getOperationDefinition() {
        return null;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        return null;
    }
}
