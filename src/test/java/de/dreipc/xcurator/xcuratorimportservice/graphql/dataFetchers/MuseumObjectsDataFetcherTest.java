package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.testutil.EntityUtil;
import de.dreipc.xcurator.xcuratorimportservice.testutil.GraphQLTestExecutor;
import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import de.dreipc.xcurator.xcuratorimportservice.testutil.WebTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({UnitTestConfiguration.class, WebTestConfiguration.class})
@Slf4j
class MuseumObjectsDataFetcherTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private GraphQLTestExecutor graphqlExecutor;

    @Autowired
    private MuseumObjectRepository museumObjectRepository;

    @BeforeEach
    void setUp() {
        this.graphqlExecutor = GraphQLTestExecutor.create(restTemplate);
        assertNotNull(museumObjectRepository);
    }

    @Test
    void testMuseumObjectQuery(@Value("classpath:graphql/museumObject.graphql") Resource graphqlQuery) throws IOException {

        var museumObject = EntityUtil.createMuseumObject();
        museumObjectRepository.save(museumObject);
        var content = StreamUtils.copyToString(graphqlQuery.getInputStream(), Charset.defaultCharset());
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("id", museumObject.getId().toString());
        var response = this.graphqlExecutor.request(content, variables);
        var data = response.get("data");
        assertNotNull(data);
    }
}