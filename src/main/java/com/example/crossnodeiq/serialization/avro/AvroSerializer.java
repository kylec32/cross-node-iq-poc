package com.example.crossnodeiq.serialization.avro;

import com.example.crossnodeiq.serialization.Serializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {
    @Getter
    private final boolean useBinaryEncoding;

    @Override
    public byte[] serialize(T data) {
        try {
            byte[] result = null;

            if (data != null) {
                if (log.isDebugEnabled()) {
                    log.debug("data={}:{}", data.getClass().getName(), data);
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Encoder encoder = useBinaryEncoding ?
                        EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null) :
                        EncoderFactory.get().jsonEncoder(data.getSchema(), byteArrayOutputStream);;

                DatumWriter<T> datumWriter = new SpecificDatumWriter<>(data.getSchema());
                datumWriter.write(data, encoder);

                encoder.flush();
                byteArrayOutputStream.close();

                result = byteArrayOutputStream.toByteArray();
                if (log.isDebugEnabled()) {
                    log.debug("serialized data='{}' ({})", "TODO", new String(result));
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Can't serialize data='" + data + "'", e);
        }
    }
}
