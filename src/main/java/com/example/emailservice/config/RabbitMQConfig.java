package com.example.emailservice.config;

import com.example.emailservice.listener.EmailListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "emailQueue";
    private static final String INTERNAL_QUEUE_NAME = "emailQueueInternal";
    private static final String EXCHANGE_NAME = "emailExchange";
    private static final String ROUTING_KEY = "emailRoutingKey";
    private static final String INTERNAL_ROUTING_KEY = "emailInternalRoutingKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue internalQueue() {
        return new Queue(INTERNAL_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding internalBinding(Queue internalQueue, TopicExchange exchange) {
        return BindingBuilder.bind(internalQueue).to(exchange).with(INTERNAL_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME, INTERNAL_QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(EmailListener emailListener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(emailListener);
        adapter.setMessageConverter(jsonMessageConverter());
        adapter.setDefaultListenerMethod("receiveMessage");
        return adapter;
    }
}
