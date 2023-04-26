package com.blitzar.cards;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@Testcontainers
public interface KafkaTestContainer {

    KafkaContainer KAFKA_CONTAINER = new KafkaContainer().withReuse(true);

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(KAFKA_CONTAINER).join();

        registry.add("spring.kafka.properties.bootstrap.servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.consumer.properties.auto.offset.reset", () -> "earliest");
    }
}


