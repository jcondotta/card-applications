package com.blitzar.cards.service.events;

import com.blitzar.cards.validation.annotation.CardholderName;

public record CardApplicationEvent(@CardholderName String cardholderName) {

}

