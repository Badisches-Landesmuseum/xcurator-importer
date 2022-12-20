package de.dreipc.xcurator.xcuratorimportservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
//        factory.setBatchSize(100);
//        factory.setBatchListener(true);
        factory.setPrefetchCount(100);
//        factory.setConsumerBatchEnabled(true);
        return factory;
    }

    @Bean
    @Qualifier("${spring.rabbitmq.exchange}")
    public TopicExchange assetsExchange() {
        return new TopicExchange("assets", true, false);
    }


}
