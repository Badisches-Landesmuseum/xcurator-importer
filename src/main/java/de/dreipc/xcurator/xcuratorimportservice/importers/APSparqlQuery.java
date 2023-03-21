package de.dreipc.xcurator.xcuratorimportservice.importers;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class APSparqlQuery {

    private static final String IMPORTS = """
            PREFIX foaf: <http://xmlns.com/foaf/0.1/>
            PREFIX dc: <http://purl.org/dc/elements/1.1/>
            PREFIX dct: <http://purl.org/dc/terms/>
            PREFIX edm: <http://www.europeana.eu/schemas/edm/>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
            PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>
            """;
    private static final String SELECT_FIELDS = """
            SELECT
            (?record as ?identifier)
            ?creator
            ?located
            ?title
            ?iiif
            (group_concat(distinct ?description;separator=" | ") as ?descriptions)
            ?abstract
            ?begin
            ?end
            """;
    private static final String SELECT_TOTAL_COUNT = """
            SELECT
            (count(?record) as ?totalCount)
            """;
    private static final String WHERE_QUERY = """
                        WHERE {
              ?record rdf:type edm:ProvidedCHO .
              ?record foaf:depiction ?iiif .
              ?record dc:title ?title .
              OPTIONAL {?record dc:description ?description. }
              OPTIONAL {?record dc:creator ?creator. }
              OPTIONAL {?record dct:spatial ?located . }
              OPTIONAL {?record dc:source ?abstract . }
              OPTIONAL {?record sem:hasBeginTimeStamp ?begin. }
              OPTIONAL {?record sem:hasEndTimeStamp ?end. }

              FILTER(!bound(?creator) || isLiteral(?creator))
              FILTER(!bound(?located) || isLiteral(?located))
            }
            """;

    private APSparqlQuery() {
        // private
    }

    public static String totalCountQuery() {
        return IMPORTS +
                SELECT_TOTAL_COUNT +
                WHERE_QUERY;
    }

    public static String getDataBatchedQuery(int take, int skip) {
        return IMPORTS +
                SELECT_FIELDS +
                WHERE_QUERY +
                "LIMIT " + take +
                "OFFSET " + skip;
    }
}
