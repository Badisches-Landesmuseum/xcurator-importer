package de.dreipc.xcurator.xcuratorimportservice.services;

public class SimilarArtefactQuery {


    private static final String QUERY_TEMPLATE = """
            {
                "from": <SKIP>,
                "size": <TAKE>,
                "collapse": {
                    "field": "reference_id"
                },
                "query": {
                    "elastiknn_nearest_neighbors": {
                        "field": "vector",
                        "vec": {
                            "index": "<INDEX_NAME>",
                            "field": "vector",
                            "id": "<IMAGE_ID>"
                        },
                        "model": "lsh",
                        "similarity": "cosine",
                        "candidates": 1000
                    }
                }
            }
            """;

    public static String query(String imageId, String projectId, int first, int skip) {
        return QUERY_TEMPLATE
                .replace("<SKIP>", String.valueOf(skip))
                .replace("<TAKE>", String.valueOf(first))
                .replace("<INDEX_NAME>", indexName(projectId))
                .replace("<IMAGE_ID>", imageId);
    }

    public static String indexName(String projectId) {
        return String.format("image-search-service_%s", projectId);
    }
}
