package com.blitzar.cards.web.controller;

import com.blitzar.cards.KafkaTestContainer;
import com.blitzar.cards.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.cards.service.events.CardApplicationEvent;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CardApplicationControllerTest implements KafkaTestContainer {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    @Qualifier("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(@LocalServerPort int serverHttpPort){
        RestAssured.port = serverHttpPort;
        RestAssured.basePath = "/api/v1/cards/application";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach() {
        this.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void givenValidRequest_whenCardApplication_thenReturnCreated(){
        var cardApplicationEvent = new CardApplicationEvent("Jefferson Condotta");

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.value())
                .body("cardApplicationReference", Matchers.notNullValue())
                .body("message", equalTo(messageSource.getMessage("card.application.accepted", null, Locale.getDefault())));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidCardholderName_whenCardApplication_thenReturnBadRequest(String invalidCardholderName){
        var cardApplicationEvent = new CardApplicationEvent(invalidCardholderName);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("cardholderName"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.cardholderName.notBlank", null, Locale.getDefault())));
    }

    @Test
    public void givenCardholderNameLongerThan21Characters_whenCardApplication_thenReturnBadRequest(){
        var invalidCardholderName = RandomStringUtils.randomAlphabetic(22);
        var cardApplicationEvent = new CardApplicationEvent(invalidCardholderName);

        given()
            .spec(requestSpecification)
            .body(cardApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("cardholderName"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("card.cardholderName.length.limit", null, Locale.getDefault())));
    }
}