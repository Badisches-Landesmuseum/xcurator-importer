package de.dreipc.xcurator.xcuratorimportservice.elasticserach;


import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObjectCountConnection;
import dreipc.common.graphql.query.OffsetBasedRequest;
import dreipc.common.graphql.query.mongodb.SortMiddlewareMongoDB;
import dreipc.graphql.types.MuseumObjectSearchOrderByInput;
import dreipc.graphql.types.MuseumObjectSearchWhereInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ArtifactIndexRepositoryImpl implements ArtifactIndexRepositoryCustom {
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    private final SortMiddlewareMongoDB sortEngine;


    public ArtifactIndexRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate, SortMiddlewareMongoDB sortEngine) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.sortEngine = sortEngine;
    }


    @Override
    public MuseumObjectCountConnection searchMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment) {
        var sorting = sortEngine.convert(orderBy, ArtifactIndex.class);
        OffsetBasedRequest pageRequest = new OffsetBasedRequest(first, skip, sorting);

        var artifactBoolQuery = QueryBuilders.boolQuery();

        try {
            where.getKeywords().forEach(keyword -> artifactBoolQuery.must(QueryBuilders.multiMatchQuery(keyword, "_id", "keywords", "topics", "titles", "descriptions", "dataSource")));
        } catch (Exception e) {
            log.info("Did not apply keywords search");
        }


        try {
            where.getCountries().forEach(country -> artifactBoolQuery.must(QueryBuilders.termQuery("countryName", country)));
        } catch (Exception e) {
            log.info("Did not apply country search");
        }

        try {
            where.getEpochs().forEach(epoch -> artifactBoolQuery.must(QueryBuilders.termQuery("epoch", epoch)));
        } catch (Exception e) {
            log.info("Did not apply epoch search");
        }

        try {
            where.getMaterials().forEach(material -> artifactBoolQuery.must(QueryBuilders.termQuery("materials", material)));
        } catch (Exception e) {
            log.info("Did not apply materials search");
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(artifactBoolQuery)
                .withTrackTotalHits(true)
                .withFields("keywords", "topics")
                .withSourceFilter(new FetchSourceFilter(new String[]{}, new String[]{"*"}))
                .withPageable(pageRequest)
                .build();

        var searchHits = elasticsearchTemplate.search(searchQuery, ArtifactIndex.class);
        var list = searchHits.stream().map(SearchHit::getContent).toList();

        var smartKeywords = new ArrayList<String>();
        smartKeywords.addAll(extractKeywords(list));
        smartKeywords.addAll(extractTopics(list));
        var keywords = smartKeywords.stream().limit(first).skip(skip).toList();

        return new MuseumObjectCountConnection(list, searchHits.getTotalHits(), environment, keywords);
    }

    @Override
    public MuseumObjectCountConnection<ArtifactIndex> exceptionalMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment) {
        return searchMuseumObjects(where, orderBy, first, skip, environment);
    }


    private List<String> extractTopics(List<ArtifactIndex> museumObjects) {
        try {
            return museumObjects
                    .stream()
                    .map(ArtifactIndex::getTopics)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Error extracting topics", e);
            return Collections.emptyList();
        }

    }

    private List<String> extractKeywords(List<ArtifactIndex> museumObjectIndices) {

        return museumObjectIndices
                .stream()
                .map(ArtifactIndex::getKeywords)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .stream()
                .toList();
    }

}
