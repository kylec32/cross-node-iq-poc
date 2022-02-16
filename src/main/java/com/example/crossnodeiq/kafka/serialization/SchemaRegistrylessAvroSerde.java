package com.example.crossnodeiq.kafka.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class SchemaRegistrylessAvroSerde<T extends org.apache.avro.specific.SpecificRecordBase> implements Serde<T> {

    @Override
    public Serializer<T> serializer() {
        return new SchemaRegistrylessAvroSerializer<>();
    }

    @Override
    public Deserializer<T> deserializer() {
        return new SchemaRegistrylessAvroDeserializer<>();
    }
}
