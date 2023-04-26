package com.blitzar.cards.service;

import com.blitzar.cards.config.kafka.KafkaApplicationProperties;
import com.blitzar.cards.service.events.CardApplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CardApplicationEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(CardApplicationEventProducer.class);

    private final KafkaTemplate<String, CardApplicationEvent> kafkaTemplate;
    private final KafkaApplicationProperties kafkaApplicationProperties;

    public CardApplicationEventProducer(KafkaTemplate<String, CardApplicationEvent> kafkaTemplate, KafkaApplicationProperties kafkaApplicationProperties) {
        this.kafkaApplicationProperties = kafkaApplicationProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handle(CardApplicationEvent cardApplicationEvent){
        logger.info("Producing a Card Application event with cardholderName: {}", cardApplicationEvent.cardholderName());
        kafkaTemplate.send(kafkaApplicationProperties.cardApplicationTopic(), cardApplicationEvent);
    }
}
