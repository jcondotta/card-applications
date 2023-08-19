package com.blitzar.cards.web.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.blitzar.cards.AWSSQSTestContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class BankAccountCreatedConsumerTest implements AWSSQSTestContainer {

    @Inject
    private AmazonSQS sqsClient;

    @Value("${app.aws.sqs.bank-account-created-queue-name}")
    private String bankAccountCreatedQueueName;

    private String bankAccountCreatedQueueURL;

    @Inject
    private ObjectMapper objectMapper;

    private String accountHolderName = "Jefferson Condotta";
    private String iban = UUID.randomUUID().toString();

    @BeforeEach
    public void beforeEach() {
        this.bankAccountCreatedQueueURL = sqsClient.getQueueUrl(bankAccountCreatedQueueName).getQueueUrl();
    }

    @AfterEach
    public void afterEach() {
        sqsClient.purgeQueue(new PurgeQueueRequest(bankAccountCreatedQueueURL));
    }

    @Test
    public void givenValidBankAccountApplicationSQSMessage_whenConsumeMessage_thenCreateBankAccount() throws JsonProcessingException {
        sqsClient.sendMessage(bankAccountCreatedQueueURL, "test");

        await().atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Message> messages = sqsClient.receiveMessage(new ReceiveMessageRequest(bankAccountCreatedQueueURL).withMaxNumberOfMessages(2)).getMessages();
                System.out.println(messages);
            });
    }
}