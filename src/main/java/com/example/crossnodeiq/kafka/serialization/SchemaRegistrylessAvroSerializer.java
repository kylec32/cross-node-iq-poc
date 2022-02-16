package com.example.crossnodeiq.kafka.serialization;

import com.example.crossnodeiq.serialization.avro.AvroSerializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

public class SchemaRegistrylessAvroSerializer<T extends org.apache.avro.specific.SpecificRecord> implements Serializer<T> {
    private final com.example.crossnodeiq.serialization.Serializer<SpecificRecordBase> serializer = new AvroSerializer<>(true);

    @Override
    public byte[] serialize(String s, T t) {
        return serializer.serialize((SpecificRecordBase) t);
    }
}
