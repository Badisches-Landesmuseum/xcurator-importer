package de.dreipc.xcurator.xcuratorimportservice.models;

import dreipc.common.graphql.relay.CountConnection;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.List;

public class MuseumObjectCountConnection<T> extends CountConnection<T> {
    private List<String> keywords = new ArrayList<>();

    public MuseumObjectCountConnection(List<T> data, long totalCount, DataFetchingEnvironment environment) {
        super(data, totalCount, environment);
    }

    public MuseumObjectCountConnection(List<T> data, long totalCount, DataFetchingEnvironment environment, List<String> keywords) {
        super(data, totalCount, environment);
        this.keywords = keywords;
    }


    public List<String> getKeywords() {
        return keywords;
    }
}
