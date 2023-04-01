package com.blitzar.cards.events;

import com.blitzar.cards.service.delegate.AddCardDelegate;
import com.blitzar.cards.validation.annotation.CardholderName;

public record CardApplicationEvent(
        @CardholderName String cardholderName) implements AddCardDelegate {
}

