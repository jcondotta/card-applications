package com.blitzar.cards.service.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class BankAccountCreatedMessage {

    @JsonProperty("Type")
    public String type;

    @JsonProperty("Message")
    public String message;
}
