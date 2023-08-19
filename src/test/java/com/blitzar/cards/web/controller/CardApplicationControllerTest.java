package com.blitzar.cards.web.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.blitzar.cards.AWSSQSTestContainer;
import com.blitzar.cards.argument_provider.InvalidStringArgumentProvider;
import com.blitzar.cards.service.events.CardApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class CardApplicationControllerTest implements AWSSQSTestContainer {

    @Inject
    private AmazonSQS sqsClient;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource messageSource;

    @Value("${app.aws.sqs.card-application-queue-name}")
    private String cardApplicationQueueName;

    private String cardApplicationQueueURL;

    @Inject
    private ObjectMapper objectMapper;

    private RequestSpecification requestSpecification;

    private String cardholderName = "Jefferson Condotta";
    private String iban = UUID.randomUUID().toString();

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(CardAPIConstants.CARD_APPLICATION_V1_MAPPING);

        this.cardApplicationQueueURL = sqsClient.getQueueUrl(cardApplicationQueueName).getQueueUrl();
    }

    @AfterEach
    public void afterEach() {
        sqsClient.purgeQueue(new PurgeQueueRequest(cardApplicationQueueURL));
    }

    @Test
    public void givenValidRequest_whenApplyForNewCard_thenReturnAccepted() throws JsonProcessingException {
        var cardApplicationEvent = new CardApplicationEvent(cardholderName, iban);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.getCode());

        List<Message> messages = sqsClient.receiveMessage(new ReceiveMessageRequest()
                        .withQueueUrl(cardApplicationQueueURL)
                        .withMaxNumberOfMessages(2))
                .getMessages();
        assertThat(messages).hasSize(1);

        for(Message message : messages) {
            var messageEvent = objectMapper.readValue(message.getBody(), CardApplicationEvent.class);

            Assertions.assertAll(
                    () -> assertThat(messageEvent.getIban()).isEqualTo(cardApplicationEvent.getIban()),
                    () -> assertThat(messageEvent.getCardholderName()).isEqualTo(cardApplicationEvent.getCardholderName())
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidCardholderName_whenApplyForNewCard_thenReturnBadRequest(String invalidAccountHolderName){
        var cardApplicationEvent = new CardApplicationEvent(invalidAccountHolderName, iban);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("must not be blank"));
    }

    @Test
    public void givenCardholderNameLongerThan21Characters_whenRegisterCardApplication_thenReturnBadRequest(){
        var invalidCardholderName = RandomStringUtils.randomAlphabetic(22);
        var cardApplicationEvent = new CardApplicationEvent(invalidCardholderName, iban);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("size must be between 0 and 21"));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidIBAN_whenApplyForNewCard_thenReturnBadRequest(String invalidIBAN){
        var cardApplicationEvent = new CardApplicationEvent(cardholderName, invalidIBAN);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("must not be blank"));
    }
}