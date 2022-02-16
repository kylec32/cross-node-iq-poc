package com.example.crossnodeiq;

import com.example.crossnodeiq.serialization.messageconverter.AvroBinaryHttpMessageConverter;
import com.example.crossnodeiq.serialization.messageconverter.AvroJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class AvroConverterConfiguration implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new AvroJsonHttpMessageConverter<>());
        converters.add(1, new AvroBinaryHttpMessageConverter<>());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        restTemplate.getMessageConverters().add(0, new AvroJsonHttpMessageConverter<>());
        restTemplate.getMessageConverters().add(1, new AvroBinaryHttpMessageConverter<>());
        return restTemplate;
    }
}
