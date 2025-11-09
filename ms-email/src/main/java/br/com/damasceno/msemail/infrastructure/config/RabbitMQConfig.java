package br.com.damasceno.msemail.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routingkey.name}")
    private String routingKey;

    @Value("${rabbitmq.dlx.name}")
    private String dlxName;
    @Value("${rabbitmq.dlq.name}")
    private String dlqName;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", routingKey)
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public Exchange dlx() {
        return ExchangeBuilder.topicExchange(dlxName).durable(true).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq())
                .to(dlx())
                .with(routingKey)
                .noargs();
    }

}
