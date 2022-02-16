package com.example.crossnodeiq.serialization.messageconverter;

import com.example.crossnodeiq.serialization.Deserializer;
import com.example.crossnodeiq.serialization.Serializer;
import com.example.crossnodeiq.serialization.avro.AvroDeserializer;
import com.example.crossnodeiq.serialization.avro.AvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class AbstractAvroHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Serializer<SpecificRecordBase> serializer;
    private final Deserializer<SpecificRecordBase> deserializer;

    protected AbstractAvroHttpMessageConverter(boolean useBinaryEncoding, MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
        serializer = new AvroSerializer<>(useBinaryEncoding);
        deserializer = new AvroDeserializer<>(useBinaryEncoding);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return SpecificRecordBase.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        T result = null;
        byte[] data = IOUtils.toByteArray(inputMessage.getBody());
        if (data.length > 0) {
            result = (T) deserializer.deserialize((Class<? extends SpecificRecordBase>) clazz, data);
        }
        return result;
    }

    @Override
    protected void writeInternal(T t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        byte[] data = serializer.serialize((SpecificRecordBase) t);
        outputMessage.getBody().write(data);
    }
}
