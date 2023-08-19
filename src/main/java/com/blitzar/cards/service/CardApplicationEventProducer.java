package com.blitzar.cards.service;

import com.blitzar.cards.service.events.CardApplicationEvent;
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public interface CardApplicationEventProducer {

    @Queue("${app.aws.sqs.card-application-queue-name}")
    void sendMessage(@MessageBody CardApplicationEvent cardApplicationEvent);

}
