package org.lu.zhaodazi.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    public static final String MATCH_START_QUEUE = "match.start.queue";
    public static final String MATCH_FINISH_QUEUE = "match.finish.queue";
    public static final String MATCH_EX = "match.exchange";
    public static final String MATCH_START_BIND = "match.start.bind";
    public static final String MATCH_FINISH_BIND = "match.finish.bind";
    public static final String IP = ".wait.queue";
    @Bean
    public Queue startQueue() {
        return new Queue(MATCH_START_QUEUE);
    }
    @Bean
    public Queue finishQueue() {
        return new Queue(MATCH_FINISH_QUEUE);
    }
    @Bean
    public DirectExchange matchExchange() {
        return new DirectExchange(MATCH_EX);
    }
    @Bean
    public Binding matchStartBinding(@Qualifier("startQueue") Queue queue, @Qualifier("matchExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MATCH_START_BIND);
    }
    @Bean
    public Binding matchFinishBinding(@Qualifier("finishQueue") Queue queue, @Qualifier("matchExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MATCH_FINISH_BIND);
    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}