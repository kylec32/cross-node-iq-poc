package com.example.crossnodeiq.serialization.messageconverter;

import org.springframework.http.MediaType;

public class AvroBinaryHttpMessageConverter<T> extends AbstractAvroHttpMessageConverter<T> {
    public AvroBinaryHttpMessageConverter() {
        super(true, new MediaType("application", "avro", DEFAULT_CHARSET),
              new MediaType("application", "*+avro", DEFAULT_CHARSET));
    }
}
