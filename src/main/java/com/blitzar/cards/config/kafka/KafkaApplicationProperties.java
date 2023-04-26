package com.blitzar.cards.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaApplicationProperties(String cardApplicationTopic) {
}
