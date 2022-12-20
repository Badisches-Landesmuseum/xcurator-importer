package de.dreipc.xcurator.xcuratorimportservice.testutil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class GraphQLTestExecutor {

    private final TestRestTemplate template;
    private final ObjectMapper jsonMapper;
    private final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
    };
    private final HttpHeaders httpHeaders = new HttpHeaders();

    public GraphQLTestExecutor(TestRestTemplate template, ObjectMapper jsonMapper) {
        this.template = template;
        this.jsonMapper = jsonMapper;
    }

    public static GraphQLTestExecutor create(TestRestTemplate restTemplate) {
        return new GraphQLTestExecutor(restTemplate, new ObjectMapper());
    }

    private static void addJWTAuthHeader(HttpHeaders headers) {
        var jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJVUd6YnpvTlV0aDN3Z2lsQ0VzMGd3QWY4MjVRRlRWRFo3WXBKY0NCNmZRIn0.eyJleHAiOjE1OTcwNjA3OTAsImlhdCI6MTU5NzA2MDQ5MCwiYXV0aF90aW1lIjoxNTk3MDYwNDkwLCJqdGkiOiIxYzE4ZjYwNS04ZDMzLTQ0OTQtOWM4Ny0zMGE0ODUzM2M4OWMiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLms4cy4zcGMuZGUvYXV0aC9yZWFsbXMvM3BjIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjAxOTFkY2M5LWEwYmYtNGNiZi1iOTJhLTYxNTI2MzAyNzliMSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdyYXBocWwtZ2F0ZXdheSIsInNlc3Npb25fc3RhdGUiOiJkNjUzNDY5MS02YmU0LTQxMzMtYmJjZi05NWFiNjZhNTFmNTkiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTb2VyZW4gUmFldWNobGUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzcmFldWNobGUiLCJnaXZlbl9uYW1lIjoiU29lcmVuIiwiZmFtaWx5X25hbWUiOiJSYWV1Y2hsZSIsImVtYWlsIjoic3JhZXVjaGxlQDNwYy5kZSJ9.Wf5xrMAOQnJYwXmolOUjia1Ro4CUBHAM6zM4Y9k7dOyhA--YKtmWs_1GLiFTqnrcTHTBXkCwVBD_sCW6eZujiVL5dn-ZNUbegTjaVxSNVU2uaYOY5yoEKX55DyzSVYT-OJq5v5bHrOTw9tY0PC1YUzZETtI41nXVDRELhRPU-Cj4s28ED5A-F0Zz7_7bwt3lVNESY2bpr_wi0xT_4ftgxV3oj3u2aZDDLOee9pO8LD0QtIGk9ZEJdgT1yE6ksIbUeHL7i8pJMZqHX0VOGdlCf3eWJTO1ctW04-OWvj0gpOVVv_n4M-trfSLEIkKNIIJ6LxdOTMFVByzEIHjiZhDcrQ";
        headers.set("Authorization", "Bearer " + jwtToken);
    }

    public GraphQLTestExecutor enableAuth() {
        addJWTAuthHeader(httpHeaders);
        return this;
    }

    public Map<String, Object> request(String query) throws IOException {
        return request(query, new LinkedHashMap<>());
    }

    public Map<String, Object> request(Resource queryFile) throws IOException {
        return request(queryFile, new LinkedHashMap<>());
    }

    public Map<String, Object> request(Resource queryFile, Map<String, Object> variables) throws IOException {
        var queryString = StreamUtils.copyToString(queryFile.getInputStream(), Charset.defaultCharset());
        return request(queryString, variables);
    }

    public Map<String, Object> request(String query, Map<String, Object> variables) throws IOException {
        String GRAPHQL_ENDPOINT = "/graphql";
        httpHeaders.add("Content-Type", "application/json");
        var requestHeader = new HttpHeaders(httpHeaders);
        var formData = new LinkedMultiValueMap<>();

        var fileVariable = variables.entrySet().stream().filter(variable -> variable.getValue() instanceof MultipartFile).findFirst();

        var graphQlParams = new LinkedHashMap<String, Object>();
        graphQlParams.put("operationName", null);
        graphQlParams.put("variables", variables);
        graphQlParams.put("query", query);

        HttpEntity requestEntity;
        if (formData.containsKey("map")) {
            formData.set("operations", jsonMapper.writeValueAsString(graphQlParams));
            requestEntity = new HttpEntity<>(formData, requestHeader);
        } else {
            requestEntity = new HttpEntity<>(graphQlParams, requestHeader);
        }

        var response = template.exchange(GRAPHQL_ENDPOINT, HttpMethod.POST, requestEntity, String.class);
        var bodyString = response.getBody();
        return jsonMapper.readValue(bodyString, typeRef);
    }
}
