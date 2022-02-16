package com.example.crossnodeiq.kafka.rpc;

import lombok.Builder;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;

@Builder
@Getter
public class StoreInfo<U, T> {
    private final String remoteRequestPath;
    private final Serializer<U> keySerializer;
    private final String storeName;
    private final Class<T> responseClass;
    private final KafkaStreams streams;
}
