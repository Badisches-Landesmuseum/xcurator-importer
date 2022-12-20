package de.dreipc.xcurator.xcuratorimportservice.testutil;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

@TestConfiguration
public class WebTestConfiguration {

    @Bean
    @Primary
    TestRestTemplate timeOutTestRestTemplate(UriBuilderFactory uriBuilderFactory) {
        var template = new TestRestTemplate();

        template.getRestTemplate().setUriTemplateHandler(uriBuilderFactory);
        template.getRestTemplate().setRequestFactory(timeoutClientHttpRequestFactory());

        return template;
    }

    // Time-Out Settings!
    private ClientHttpRequestFactory timeoutClientHttpRequestFactory() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);     // 5 seconds
        requestFactory.setReadTimeout(5000);        // 5 seconds
        return requestFactory;
    }

    @Bean
        // Needed to construct a TestRestTemplate using the right rootUrl!
    UriBuilderFactory uriBuilderFactory(ServletWebServerApplicationContext webServerAppCtxt) {
        var port = webServerAppCtxt.getWebServer().getPort();
        var rootUri = "http://localhost:" + port;
        return new DefaultUriBuilderFactory(rootUri);
    }

    @Bean
    GraphQLTestExecutor graphQLTestExecutor(TestRestTemplate template) {
        return GraphQLTestExecutor
                .create(template)
                .enableAuth();
    }


}
