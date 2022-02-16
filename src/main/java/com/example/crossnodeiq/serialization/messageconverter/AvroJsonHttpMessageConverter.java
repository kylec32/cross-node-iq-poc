package com.example.crossnodeiq.serialization.messageconverter;

import org.springframework.http.MediaType;

public class AvroJsonHttpMessageConverter<T> extends AbstractAvroHttpMessageConverter<T> {
    public AvroJsonHttpMessageConverter() {
        super(false, new MediaType("application", "avro+json", DEFAULT_CHARSET),
              new MediaType("application", "*+avro+json", DEFAULT_CHARSET));
    }
}
