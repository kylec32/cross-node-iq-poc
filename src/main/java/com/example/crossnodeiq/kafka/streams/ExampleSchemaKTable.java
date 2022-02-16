package com.example.crossnodeiq.kafka.streams;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExampleSchemaKTable {
    public static final String STORE_NAME = "example-schema-store";

    @Bean
    @Lazy(false)
    public KTable<Integer, ExampleSchema> exampleSchemaTable(final KStream<Integer, ExampleSchema> exampleSchemaKStream) {
        log.info("exampleSchemaKTable");
        return exampleSchemaKStream.groupByKey()
                .aggregate(ExampleSchema::new, (key, newValue, aggValue) -> newValue,
                           Materialized.<Integer, ExampleSchema, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
                                   .withKeySerde(Serdes.Integer())
                                   .withValueSerde(new SchemaRegistrylessAvroSerde<>()));
    }
}
