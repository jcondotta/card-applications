package com.blitzar.cards.service;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.blitzar.cards.service.dto.AccountHolderDTO;
import com.blitzar.cards.service.events.BankAccountCreatedMessage;
import com.blitzar.cards.service.dto.BankAccountDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jms.JMSException;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BankAccountCreatedConsumerUnitTest {

    private BankAccountCreatedConsumer bankAccountCreatedConsumer;

    @Mock
    private CardApplicationEventProducer eventProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SQSTextMessage sqsTextMessage;

    private String cardHolderName = "Jefferson Condotta";
    private String iban = UUID.randomUUID().toString();

    @BeforeEach
    public void beforeEach(){
        bankAccountCreatedConsumer = new BankAccountCreatedConsumer(eventProducer, objectMapper);
    }

    @Test
    public void givenValidRequest_whenRegisterCardApplication_thenSendMessage() throws JsonProcessingException, JMSException {
        var objectMapperReal = new ObjectMapper().registerModule(new JavaTimeModule());
        BankAccountDTO bankAccountDTO = new BankAccountDTO(UUID.randomUUID().toString(), List.of(new AccountHolderDTO("Jefferson Condotta")));

        BankAccountCreatedMessage bankAccountCreatedMessage = new BankAccountCreatedMessage();
        bankAccountCreatedMessage.type = "Notification";
        bankAccountCreatedMessage.message = objectMapperReal.writeValueAsString(bankAccountDTO);

        Mockito.when(objectMapper.readValue(sqsTextMessage.getText(), BankAccountCreatedMessage.class)).thenReturn(bankAccountCreatedMessage);
        bankAccountCreatedConsumer.consumeMessage(sqsTextMessage);
    }
}
