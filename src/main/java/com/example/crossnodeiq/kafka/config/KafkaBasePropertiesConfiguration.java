package com.example.crossnodeiq.kafka.config;

import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroDeserializer;
import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroSerializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaBasePropertiesConfiguration {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.sasl.jaas.config}")
    private String saslJaasConfig;
    @Value("${kafka.sasl.mechanism}")
    private String saslMechanism;
    @Value("${kafka.security.protocol}")
    private String securityProtocol;

    @Bean
    public Properties kafkaBaseProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("sasl.jaas.config", saslJaasConfig);
        properties.put("sasl.mechanism", saslMechanism);
        properties.put("security.protocol", securityProtocol);
        properties.put("key.serializer", IntegerSerializer.class);
        properties.put("value.serializer", SchemaRegistrylessAvroSerializer.class);
        properties.put("key.deserializer", IntegerDeserializer.class);
        properties.put("value.deserializer", SchemaRegistrylessAvroDeserializer.class);

       return properties;
    }
}
