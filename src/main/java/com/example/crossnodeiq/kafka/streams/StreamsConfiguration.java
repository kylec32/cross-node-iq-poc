package com.example.crossnodeiq.kafka.streams;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroDeserializer;
import com.example.crossnodeiq.kafka.serialization.SchemaRegistrylessAvroSerde;
import io.confluent.common.utils.TestUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class StreamsConfiguration {
    @Value("${kafka.topic.name}")
    private String topicName;

    @Bean
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }

    @Bean
    public KStream<Integer, ExampleSchema> exampleSchemaKStream(final StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(topicName, Consumed.with(Serdes.Integer(), new SchemaRegistrylessAvroSerde<ExampleSchema>()));
    }

    @Bean
    public Properties baseStreamConfig(HostInfo hostInfo, @Qualifier("kafkaBaseProperties") Properties baseProperties) throws UnknownHostException {
        final Properties properties = new Properties();
        properties.putAll(baseProperties);
        properties.put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE);
        properties.put("ssl.endpoint.identification.algorithm", "https");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", 300);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer", SchemaRegistrylessAvroDeserializer.class);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "example-app");
        properties.put("replication.factor", 1);
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
                       TestUtils.tempDirectory().getAbsolutePath());
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostInfo.host() + ":" + hostInfo.port());

        return properties;
    }

    @Bean
    public KafkaStreams appStreams(final StreamsBuilder streamsBuilder,
                                   final KStream<Integer, ExampleSchema> exampleSchemaKStream,
                                   final KTable<Integer, ExampleSchema> exampleSchemaKTable,
                                   final Properties baseStreamConfig) {
        var topology = streamsBuilder.build();
        KafkaStreams streams = new KafkaStreams(topology, baseStreamConfig);
        streams.cleanUp();
        streams.start();

        return streams;
    }
}
