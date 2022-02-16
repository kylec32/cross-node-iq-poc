package com.example.crossnodeiq.kafka.config;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ProducerConfiguration {

    @Bean
    public Properties producerProperties(@Qualifier("kafkaBaseProperties") Properties baseProperties) {
        Properties properties = new Properties();
        properties.putAll(baseProperties);
        properties.put("acks", "all");
        properties.put("compression.type", "snappy");
        properties.put("retries", "3");
        properties.put("ssl.endpoint.identification.algorithm", "");
        properties.put("key.serializer", IntegerSerializer.class);
        properties.put("value.serializer", SchemaRegistrylessAvroSerializer.class);

        return properties;
    }

    @Bean
    public Producer<Integer, ExampleSchema> producer(@Qualifier("producerProperties") Properties properties) {
        return new KafkaProducer<>(properties);
    }
}
