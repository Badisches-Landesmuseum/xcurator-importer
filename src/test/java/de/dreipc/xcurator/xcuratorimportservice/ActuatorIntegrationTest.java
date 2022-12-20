package de.dreipc.xcurator.xcuratorimportservice;

import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@Import({UnitTestConfiguration.class})
class ActuatorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Health Endpoint responds with 200")
    void applicationHealthEndpoint() {
        var healthResponse = restTemplate.getForEntity("http://localhost:" + port + "/manage/health/liveness", String.class);

        assertEquals(HttpStatus.OK, healthResponse.getStatusCode(), "Health Endpoint not responds with HTTP 200");
    }

    @Test
    @DisplayName("Application Metrics Endpoint responds with 200")
    void applicationMetricsEndpoint() {
        var infoResponse = restTemplate.getForEntity("http://localhost:" + port + "/manage/metrics", String.class);

        assertEquals(HttpStatus.OK, infoResponse.getStatusCode(), "Info Endpoint not responds with HTTP 200");
        var body = infoResponse.getBody();
        assertNotNull(body);
        assertTrue(body.contains("jvm"), "Missing JVM Metrics");
//       assertTrue(body.contains("rabbit"), "Missing RabbitMQ Metrics");
//      assertTrue(body.contains("mongodb"), "Missing MongoDB Metrics");  Not visible inside Integration Tests
//      assertTrue(body.contains("gql"), "Missing GraphQL Metrics");      Not visible inside Integration Tests
    }

    @Test
    @DisplayName("Ready Endpoint responds with 200")
    void applicationReadyEndpoint() {
        var ready = restTemplate.getForEntity("http://localhost:" + port + "/manage/health/readiness", String.class);

        assertEquals(HttpStatus.OK, ready.getStatusCode(), "Ready Endpoint not responds with HTTP 200");
    }
}