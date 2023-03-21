package de.dreipc.xcurator.xcuratorimportservice.services;

import dreipc.graphql.types.MuseumObjectSearchWhereInput;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArtifactsSearchQuery {


    private final static String QUERY = """   
            {
                "size": <SIZE>,
                "from": <FROM>,
                "_source": ["_id", "keywords"],
                "track_total_hits": <TOTAL_HITS_TRACKER>,
                "query": 
                 <QUERY_BODY>
               
            }
            """;

    public static String getQuery(MuseumObjectSearchWhereInput where, int first, int skip) {
        return QUERY
                .replace("<QUERY_BODY>", boolQuery(where))
                .replace("<TOTAL_HITS_TRACKER>", Boolean.TRUE.toString())
                .replace("<FROM>", skip + "")
                .replace("<SIZE>", first + "");
    }

    public static String boolQuery(MuseumObjectSearchWhereInput where) {

        var artifactBoolQuery = QueryBuilders.boolQuery();

        try {
            where.getKeywords().forEach(keyword -> artifactBoolQuery.must(QueryBuilders.termQuery("keywords.keyword", keyword)));
        } catch (Exception e) {
            log.info("Did not apply keywords search");
        }

        try {
            where.getCountries().forEach(country -> artifactBoolQuery.must(QueryBuilders.termQuery("location.countryName.keyword", country)));
        } catch (Exception e) {
            log.info("Did not apply country search");
        }

        try {
            where.getEpochs().forEach(epoch -> artifactBoolQuery.must(QueryBuilders.termQuery("dateRange.epoch.keyword", epoch)));
        } catch (Exception e) {
            log.info("Did not apply epoch search");
        }

        try {
            where.getMaterials().forEach(material -> artifactBoolQuery.must(QueryBuilders.termQuery("materials.keyword", material)));
        } catch (Exception e) {
            log.info("Did not apply materials search");
        }

        return artifactBoolQuery.toString();

    }


}
