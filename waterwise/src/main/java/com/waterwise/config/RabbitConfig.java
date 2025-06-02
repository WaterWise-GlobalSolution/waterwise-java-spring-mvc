package com.waterwise.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitConfig {

    // Exchange
    public static final String WATERWISE_EXCHANGE = "waterwise.exchange";

    // Queues
    public static final String PROPRIEDADE_QUEUE = "waterwise.propriedade.queue";
    public static final String ALERTA_QUEUE = "waterwise.alerta.queue";

    // Routing Keys
    public static final String PROPRIEDADE_CREATED_KEY = "propriedade.created";
    public static final String ALERTA_GENERATED_KEY = "alerta.generated";

    @Bean
    public TopicExchange waterWiseExchange() {
        return new TopicExchange(WATERWISE_EXCHANGE);
    }

    @Bean
    public Queue propriedadeQueue() {
        return QueueBuilder.durable(PROPRIEDADE_QUEUE).build();
    }

    @Bean
    public Queue alertaQueue() {
        return QueueBuilder.durable(ALERTA_QUEUE).build();
    }

    @Bean
    public Binding propriedadeBinding() {
        return BindingBuilder
                .bind(propriedadeQueue())
                .to(waterWiseExchange())
                .with(PROPRIEDADE_CREATED_KEY);
    }

    @Bean
    public Binding alertaBinding() {
        return BindingBuilder
                .bind(alertaQueue())
                .to(waterWiseExchange())
                .with(ALERTA_GENERATED_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
