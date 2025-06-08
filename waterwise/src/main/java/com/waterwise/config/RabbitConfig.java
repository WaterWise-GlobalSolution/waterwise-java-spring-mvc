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

    // ===== CONFIGURAÇÕES EXISTENTES (MANTIDAS) =====
    // Exchange
    public static final String WATERWISE_EXCHANGE = "waterwise.exchange";

    // Queues existentes
    public static final String PROPRIEDADE_QUEUE = "waterwise.propriedade.queue";
    public static final String ALERTA_QUEUE = "waterwise.alerta.queue";

    // Routing Keys existentes
    public static final String PROPRIEDADE_CREATED_KEY = "propriedade.created";
    public static final String ALERTA_GENERATED_KEY = "alerta.generated";

    // ===== NOVAS CONFIGURAÇÕES PARA INTEGRAÇÃO .NET =====
    // Novas filas para receber dados do .NET
    public static final String SENSOR_DATA_QUEUE = "waterwise.sensor.data";
    public static final String DOTNET_ALERTS_QUEUE = "waterwise.dotnet.alerts";

    // Novas routing keys para dados do .NET
    public static final String SENSOR_DATA_KEY = "sensor.data.*";
    public static final String DOTNET_ALERTS_KEY = "alerts.*";

    // ===== BEANS EXISTENTES (MANTIDOS) =====
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

    // ===== NOVOS BEANS PARA INTEGRAÇÃO .NET =====

    /**
     * Fila para receber dados de sensores do .NET
     */
    @Bean
    public Queue sensorDataQueue() {
        return QueueBuilder.durable(SENSOR_DATA_QUEUE).build();
    }

    /**
     * Fila para receber alertas do .NET
     */
    @Bean
    public Queue dotnetAlertsQueue() {
        return QueueBuilder.durable(DOTNET_ALERTS_QUEUE).build();
    }

    /**
     * Binding para dados de sensores vindos do .NET
     */
    @Bean
    public Binding sensorDataBinding() {
        return BindingBuilder
                .bind(sensorDataQueue())
                .to(waterWiseExchange())
                .with(SENSOR_DATA_KEY);
    }

    /**
     * Binding para alertas vindos do .NET
     */
    @Bean
    public Binding dotnetAlertsBinding() {
        return BindingBuilder
                .bind(dotnetAlertsQueue())
                .to(waterWiseExchange())
                .with(DOTNET_ALERTS_KEY);
    }

    // ===== BEANS COMPARTILHADOS (MANTIDOS) =====
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

        // Configurações adicionais para o consumer
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);
        factory.setPrefetchCount(10);

        return factory;
    }
}