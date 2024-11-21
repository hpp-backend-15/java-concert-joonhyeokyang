package com.joonhyeok.app.common.config.kafka;

import com.joonhyeok.app.user.domain.pay.PayEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;


    @Bean
    public ConsumerFactory<String, PayEvent> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, keyDeserializer);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, PayEventDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PayEvent>
    payKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PayEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(customErrorHandler());
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    private DefaultErrorHandler customErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) ->
                log.error(
                        "[Error] topic = {}, key = {}, value = {}, error message = {}",
                        consumerRecord.topic(),
                        consumerRecord.key(),
                        consumerRecord.value(),
                        exception.getMessage()), new FixedBackOff(1000L, 5));
        errorHandler.addNotRetryableExceptions(RuntimeException.class);
        return errorHandler;
    }
}
