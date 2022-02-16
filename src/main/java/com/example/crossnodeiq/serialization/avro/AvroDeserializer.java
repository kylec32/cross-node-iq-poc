package com.example.crossnodeiq.serialization.avro;

import com.example.crossnodeiq.serialization.Deserializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    @Getter
    private final boolean useBinaryEncoding;

    @Override
    public T deserialize(Class<? extends T> clazz, byte[] data) {
        try {
            T result = null;
            if (data != null) {
                if (log.isDebugEnabled()) {
                    log.debug("data='{}' ({})", "To be implemented", new String(data));
                }

                Schema schema = clazz.newInstance().getSchema();
                DatumReader<T> datumReader =
                        new SpecificDatumReader<>(schema);
                Decoder decoder = useBinaryEncoding ?
                        DecoderFactory.get().binaryDecoder(data, null) :
                        DecoderFactory.get().jsonDecoder(schema, new ByteArrayInputStream(data));;

                result = datumReader.read(null, decoder);
                if (log.isDebugEnabled()) {
                    log.debug("deserialized data={}:{}", clazz.getName(), result);
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            throw new RuntimeException("Can't deserialize data '" + Arrays.toString(data) + "'", e);
        }
    }
}
