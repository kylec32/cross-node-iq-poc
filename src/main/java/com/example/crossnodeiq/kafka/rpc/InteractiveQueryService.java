package com.example.crossnodeiq.kafka.rpc;

import com.example.crossnodeiq.authentication.models.UserPrincipal;
import com.example.crossnodeiq.serialization.avro.AvroSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractiveQueryService {
    private final HostInfo hostInfo;
    private final RestTemplate restTemplate;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final AvroSerializer<SpecificRecordBase> avroSerializer = new AvroSerializer<>(true);

        public <T, U> Optional<T> query(@NonNull final StoreInfo<U, T> storeInfo,
                                        @NonNull final U key,
                                        @NonNull final Function<U, Optional<T>> localProducer) {

            KeyQueryMetadata metadata = storeInfo.getStreams().queryMetadataForKey(storeInfo.getStoreName(),
                                                                                   key,
                                                                                   storeInfo.getKeySerializer());

        if (metadata.activeHost().equals(hostInfo)) {
            log.info("Local request. " + metadata.activeHost());
            return localProducer.apply(key);
        } else {
            log.info("Remote request. " + metadata.activeHost());

            RequestData requestData = getRequestData(key);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", requestData.getContentType());
            addAuthorizationHeader(headers);
            HttpEntity<?> entity = new HttpEntity<>(requestData.getBody(), headers);

            var url = "http://"
                    + metadata.activeHost().host()
                    + ":" + metadata.activeHost().port()
                    + "/" + storeInfo.getRemoteRequestPath();
            ResponseEntity<T> response = restTemplate.exchange(url,
                                  HttpMethod.POST,
                                  entity,
                                  storeInfo.getResponseClass()
                                  );
            if (response.getStatusCode() == HttpStatus.NOT_FOUND || response.getBody() == null) {
                return Optional.empty();
            } else {
                return Optional.of(response.getBody());
            }
        }
    }

    private static void addAuthorizationHeader(@NonNull final HttpHeaders httpHeaders) {
        String token = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getToken();
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    @SneakyThrows
    private <U> RequestData getRequestData(@NonNull final U key) {
        if (key instanceof SpecificRecordBase) {
            return new RequestData("application/avro", avroSerializer.serialize((SpecificRecordBase) key));
        } else if(key instanceof String) {
          return new RequestData(MediaType.TEXT_PLAIN_VALUE, ((String) key).getBytes(StandardCharsets.UTF_8));
        } else {
            return new RequestData(MediaType.APPLICATION_JSON_VALUE, jsonMapper.writeValueAsBytes(key));
        }
    }

    @Value
    private static class RequestData {
        String contentType;
        byte[] body;
    }
}
