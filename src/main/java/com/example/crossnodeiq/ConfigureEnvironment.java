package com.example.crossnodeiq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConfigureEnvironment {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String emailAddress = "";
        final String apiKey = "";

        HttpClient client = HttpClient.newHttpClient();

        var authorizationHeader = Base64.getEncoder().encodeToString((emailAddress + ":" + apiKey).getBytes(StandardCharsets.UTF_8));

        System.out.print("Creating cluster...");
        CreateClusterResponse createClusterResponse = createCluster(client, authorizationHeader);
        System.out.println("success.");

        System.out.print("Creating event topic...");
        createTopic(client,
                    createClusterResponse.getClusterId(),
                    "event-topic",
                    authorizationHeader);
        System.out.println("success.");

        System.out.print("Creating changelog topic...");
        createTopic(client,
                    createClusterResponse.getClusterId(),
                    "example-app-example-schema-store-changelog",
                    authorizationHeader);
        System.out.println("success.");


        System.out.println("\nCompleted please replace the existing application.properties with the below values.");
        System.out.println("kafka.bootstrap.servers=" + createClusterResponse.getTcpEndpoint()+":9092");
        System.out.println("kafka.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username=\""
                                   + createClusterResponse.getEncodedUsername()
                                   + "\" password=\""
                                   + createClusterResponse.getPassword()
                                   + "\";");
        System.out.println("kafka.sasl.mechanism=SCRAM-SHA-256");
        System.out.println("kafka.security.protocol=SASL_SSL");

    }

    private static CreateClusterResponse createCluster(final HttpClient client,
                                                        final String authorizationValue) throws IOException, InterruptedException {
        CreateClusterRequest createClusterRequestBody = new CreateClusterRequest("cross-node-iq", "us-east-1", false);

        HttpRequest createClusterRequest = HttpRequest.newBuilder()
                                                      .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(createClusterRequestBody)))
                                                      .uri(java.net.URI.create("https://api.upstash.com/v2/kafka/cluster"))
                                                      .header("Content-Type", "application/json")
                                                      .header("Authorization", "Basic " + authorizationValue)
                                                      .build();
        return new ObjectMapper().readValue(client.send(createClusterRequest, HttpResponse.BodyHandlers.ofString()).body(), CreateClusterResponse.class);
    }

    private static void createTopic(final HttpClient client,
                                    final String clusterId,
                                    final String topicName,
                                    final String authorizationValue) throws IOException, InterruptedException {
        CreateTopicRequest createChangelogTopicRequestBody = new CreateTopicRequest(topicName,
                                                                                    2,
                                                                                    604800000,
                                                                                    268435456,
                                                                                    1048576,
                                                                                    "delete",
                                                                                    clusterId);

        HttpRequest createChangelogTopicRequest = HttpRequest.newBuilder()
                                                             .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(createChangelogTopicRequestBody)))
                                                             .uri(java.net.URI.create("https://api.upstash.com/v2/kafka/topic"))
                                                             .header("Content-Type", "application/json")
                                                             .header("Authorization", "Basic " + authorizationValue)
                                                             .build();

        client.send(createChangelogTopicRequest, HttpResponse.BodyHandlers.ofString()).body();
    }

    @Data
    @RequiredArgsConstructor
    private static class CreateClusterRequest {
        private final String name;
        private final String region;
        private final boolean multizone;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CreateClusterResponse {
        @JsonProperty("cluster_id")
        private String clusterId;
        private String username;
        private String password;
        @JsonProperty("tcp_endpoint")
        private String tcpEndpoint;
        @JsonProperty("encoded_username")
        private String encodedUsername;
    }

    @Data
    private static class CreateTopicRequest {

        private final String name;
        private final int partitions;
        @JsonProperty("retention_time")
        private final long retentionTime;
        @JsonProperty("retention_size")
        private final long retentionSize;
        @JsonProperty("max_message_size")
        private final long maxMessageSize;
        @JsonProperty("cleanup_policy")
        private final String cleanupPolicy;
        @JsonProperty("cluster_id")
        private final String clusterId;
    }
}
