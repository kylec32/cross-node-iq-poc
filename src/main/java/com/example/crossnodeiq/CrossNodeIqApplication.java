package com.example.crossnodeiq;

import com.example.crossnodeiq.authentication.models.User;
import com.example.crossnodeiq.authentication.services.TokenService;
import com.example.crossnodeiq.event.model.ExampleSchema;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
public class CrossNodeIqApplication implements CommandLineRunner {
	private final TokenService tokenService;
	private final Producer<Integer, ExampleSchema> producer;
	@Value("${kafka.topic.name}")
	private String topicName;

	public static void main(String[] args) {
		SpringApplication.run(CrossNodeIqApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println(tokenService.generateToken(new User(1, "testUser")));

		for(int i = 0; i < 50; i++) {
			ProducerRecord<Integer, ExampleSchema> producerRecord = new ProducerRecord<>(topicName, i, ExampleSchema.newBuilder()
																											.setId(Integer.toString(i))
																											.setTitle("title_" + i)
																											.setMaxAge(i * 30)
																													.build());

			producer.send(producerRecord);
		}
		System.out.println("Populated topic");
	}
}
