package com.example.crossnodeiq.kafka.query;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.kafka.rpc.InteractiveQueryService;
import com.example.crossnodeiq.kafka.rpc.StoreInfo;
import com.example.crossnodeiq.kafka.streams.ExampleSchemaKTable;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExampleQueryService {
    private ReadOnlyKeyValueStore<Integer, ExampleSchema> store;
    private final KafkaStreams streams;
    private final InteractiveQueryService interactiveQueryService;
    private final StoreInfo<Integer, ExampleSchema> storeInfo;

    public ExampleQueryService(KafkaStreams streams, InteractiveQueryService interactiveQueryService) {
        this.streams = streams;
        this.interactiveQueryService = interactiveQueryService;
        storeInfo = StoreInfo.<Integer, ExampleSchema>builder()
                            .storeName(ExampleSchemaKTable.STORE_NAME)
                            .keySerializer(Serdes.Integer().serializer())
                            .remoteRequestPath("example/lookup-avro")
                            .responseClass(ExampleSchema.class)
                            .streams(streams)
                            .build();

    }

    private ReadOnlyKeyValueStore<Integer, ExampleSchema> getStore() {
        if (store == null) {
            store = streams
                    .store(StoreQueryParameters
                                   .fromNameAndType(
                                           ExampleSchemaKTable.STORE_NAME,
                                           QueryableStoreTypes.keyValueStore()));
        }
        return store;
    }

    public Optional<ExampleSchema> get(final Integer key) {
        return interactiveQueryService.query(storeInfo,
                                               key,
                                               storeId -> Optional.ofNullable(getStore().get(storeId)));
    }
}
