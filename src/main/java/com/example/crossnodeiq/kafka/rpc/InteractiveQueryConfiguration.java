package com.example.crossnodeiq.kafka.rpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class InteractiveQueryConfiguration {
    private final int portNumber;

    public InteractiveQueryConfiguration(
            @Value("${server.port}") int port
    ) {
        this.portNumber = port;
    }

    @Bean
    public HostInfo currentServiceHostInfo() throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostName();
        log.info("Current host seems to be: " + hostname + ":" + portNumber);
        return new HostInfo(hostname, portNumber);
    }
}
