package com.example.crossnodeiq.kafka.serialization;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.serialization.avro.AvroDeserializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

public class SchemaRegistrylessAvroDeserializer<T extends org.apache.avro.specific.SpecificRecord> implements Deserializer<T> {
    private final com.example.crossnodeiq.serialization.Deserializer<SpecificRecordBase> deserializer = new AvroDeserializer<>(true);

    @Override
    public T deserialize(String s, byte[] bytes) {
        return (T) deserializer.deserialize(ExampleSchema.class, bytes);
    }
}
