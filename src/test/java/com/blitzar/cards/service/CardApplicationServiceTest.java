package com.blitzar.cards.service;

import com.blitzar.cards.argument_provider.InvalidStringArgumentProvider;
import com.blitzar.cards.service.events.CardApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CardApplicationServiceTest {

    private CardApplicationService cardApplicationService;
    private Validator validator;

    @Mock
    private CardApplicationEventProducer eventProducer;

    private String cardholderName = "Jefferson Condotta";
    private String iban = UUID.randomUUID().toString();

    @BeforeEach
    public void beforeEach(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        cardApplicationService = new CardApplicationService(eventProducer, validator);
    }

    @Test
    public void givenValidRequest_whenRegisterCardApplication_thenSendMessage() throws JsonProcessingException {
        var cardApplicationEvent = new CardApplicationEvent(cardholderName, iban);

        cardApplicationService.registerApplication(cardApplicationEvent);

        verify(eventProducer).sendMessage(cardApplicationEvent);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidCardholderName_whenRegisterCardApplication_thenThrowException(String invalidCardholderName){
        var cardApplicationEvent = new CardApplicationEvent(invalidCardholderName, iban);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> cardApplicationService.registerApplication(cardApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be blank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName")
                ));

        verify(eventProducer, never()).sendMessage(cardApplicationEvent);
    }

    @Test
    public void givenCardholderNameLongerThan21Characters_whenRegisterCardApplication_thenThrowException(){
        var invalidCardholderName = RandomStringUtils.randomAlphabetic(22);
        var cardApplicationEvent = new CardApplicationEvent(invalidCardholderName, iban);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> cardApplicationService.registerApplication(cardApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("size must be between 0 and 21"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("cardholderName")
                ));

        verify(eventProducer, never()).sendMessage(cardApplicationEvent);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidIBAN_whenRegisterCardApplication_thenThrowException(String invalidIBAN){
        var cardApplicationEvent = new CardApplicationEvent(cardholderName, invalidIBAN);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> cardApplicationService.registerApplication(cardApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be blank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("iban")
                ));

        verify(eventProducer, never()).sendMessage(cardApplicationEvent);
    }
}