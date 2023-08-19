package com.blitzar.cards.service;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.blitzar.cards.service.events.BankAccountCreatedMessage;
import com.blitzar.cards.service.dto.BankAccountDTO;
import com.blitzar.cards.service.events.CardApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Message;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

@Singleton
@JMSListener(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public class BankAccountCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountCreatedConsumer.class);

    private CardApplicationEventProducer cardApplicationEventProducer;
    private ObjectMapper objectMapper;

    @Inject
    public BankAccountCreatedConsumer(CardApplicationEventProducer cardApplicationEventProducer, ObjectMapper objectMapper) {
        this.cardApplicationEventProducer = cardApplicationEventProducer;
        this.objectMapper = objectMapper;
    }

    @Queue(value = "${app.aws.sqs.bank-account-created-queue-name}", concurrency = "1-3")
    public void consumeMessage(@Message SQSTextMessage sqsTextMessage) {
        logger.info(sqsTextMessage.toString());

        try {
            BankAccountDTO bankAccountDTO = objectMapper.readValue(sqsTextMessage.getText(), BankAccountDTO.class);

            bankAccountDTO.getAccountHolders().stream().forEachOrdered(accountHolderDTO -> {
                logger.info("Applying a new card for: " + accountHolderDTO.getAccountHolderName());
                cardApplicationEventProducer.sendMessage(new CardApplicationEvent(accountHolderDTO.getAccountHolderName(), bankAccountDTO.getIban()));
            });
        }
        catch (JsonProcessingException | JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
