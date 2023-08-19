package com.blitzar.cards.web.controller;

import com.blitzar.cards.service.CardApplicationService;
import com.blitzar.cards.service.events.CardApplicationEvent;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

@Validated
@Controller(CardAPIConstants.BASE_PATH_API_V1_MAPPING)
public class CardApplicationController {

    private final CardApplicationService cardApplicationService;

    @Inject
    public CardApplicationController(CardApplicationService cardApplicationService) {
        this.cardApplicationService = cardApplicationService;
    }

    @Status(HttpStatus.ACCEPTED)
    @Post(value = "/application", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> registerCardApplication(@Body CardApplicationEvent cardApplicationEvent){
        cardApplicationService.registerApplication(cardApplicationEvent);

        return HttpResponse.accepted().body("Your card application has been accepted and will be processed soon");
    }
}
