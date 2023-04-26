package com.blitzar.cards.web.controller;

import com.blitzar.cards.service.events.CardApplicationEvent;
import com.blitzar.cards.service.CardApplicationEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardApplicationController {

    private CardApplicationEventProducer cardApplicationEventProducer;
    private MessageSource messageSource;

    @Autowired
    public CardApplicationController(CardApplicationEventProducer cardApplicationEventProducer, MessageSource messageSource) {
        this.cardApplicationEventProducer = cardApplicationEventProducer;
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/application", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerCardApplication(@Valid @RequestBody CardApplicationEvent cardApplicationEvent, WebRequest request){
        cardApplicationEventProducer.handle(cardApplicationEvent);
        var cardApplicationConfirmationDTO = new CardApplicationConfirmationDTO(UUID.randomUUID().toString(), messageSource.getMessage("card.application.accepted", null, request.getLocale()));

        return ResponseEntity.accepted().body(cardApplicationConfirmationDTO);
    }

    private record CardApplicationConfirmationDTO(String cardApplicationReference, String message){}
}
