package de.dreipc.xcurator.xcuratorimportservice.testutil;


import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndexRepository;
import de.dreipc.xcurator.xcuratorimportservice.repositories.*;
import de.dreipc.xcurator.xcuratorimportservice.testutil.stubs.*;
import de.dreipc.xcurator.xcuratorimportservice.testutil.stubs.elasticsearch.TestArtifactIndexRepositoryStub;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@TestConfiguration
@MockBean(classes = {MongoTemplate.class, MappingMongoConverter.class, ElasticsearchOperations.class, ElasticsearchRestTemplate.class})
@EnableMongoRepositories(basePackages = {"de.dreipc.xcurator.xcuratorimportservice.testutil.stubs"})
@EnableElasticsearchRepositories(basePackages = "de.dreipc.xcurator.xcuratorimportservice.testutil.stubs.elasticsearch")
@EnableAutoConfiguration(exclude = {ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
public class UnitTestConfiguration {

    @Bean
    @Primary
    ProtoPublisher publisherStub() {
        return new TestProtoPublisher();
    }

    @Bean
    @Primary
    public ClientHttpRequestFactory timeoutClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(1000);//ms read time
        factory.setConnectTimeout(500);//ms connection time
        return factory;
    }

    @Bean
    @Primary
    public MuseumObjectRepository museumObjectRepository() {
        return new TestMuseumObjectRepositoryStub();
    }

    @Bean
    @Primary
    public NamedEntityRepository namedEntitiesRepository() {
        return new TestNamedEntityRepositoryStub();
    }

    @Bean
    @Primary
    public TopicRepository topicRepository() {
        return new TestTopicRepositoryStub();
    }

    @Bean
    @Primary
    public TextContentRepository textContentRepository() {
        return new TestTextContentRepositoryStub();
    }


    @Bean
    @Primary
    public StoryRepository storyRepository() {
        return new TestStoryRepositoryStub();
    }

    @Bean
    @Primary
    public ModuleRepository moduleRepository() {
        return new TestModuleRepositoryStub();
    }


    @Bean
    @Primary
    public ArtifactIndexRepository artifactIndexRepository() {
        return new TestArtifactIndexRepositoryStub();
    }


}
