package com.blitzar.cards.service;

import com.blitzar.cards.service.events.CardApplicationEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@Singleton
public class CardApplicationService {

    @Inject
    private final CardApplicationEventProducer eventProducer;
    private final Validator validator;

    public CardApplicationService(CardApplicationEventProducer eventProducer, Validator validator) {
        this.eventProducer = eventProducer;
        this.validator = validator;
    }

    public void registerApplication(CardApplicationEvent cardApplicationEvent){
        var eventValidations = validator.validate(cardApplicationEvent);
        if(!eventValidations.isEmpty()){
            throw new ConstraintViolationException(eventValidations);
        }

        eventProducer.sendMessage(cardApplicationEvent);
    }
}
