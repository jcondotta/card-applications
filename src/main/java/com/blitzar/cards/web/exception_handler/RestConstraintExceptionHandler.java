package com.blitzar.cards.web.exception_handler;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;

@Produces
@Singleton
@Replaces(value = ConstraintExceptionHandler.class)
@Requires(classes = {ConstraintViolationException.class, ExceptionHandler.class})
public class RestConstraintExceptionHandler extends ConstraintExceptionHandler {

    private final MessageSource messageSource;

    @Inject
    public RestConstraintExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        super(errorResponseProcessor);
        this.messageSource = messageSource;
    }

    @Override
    @Status(value = HttpStatus.BAD_REQUEST)
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        return super.handle(request, exception);
    }

    protected String buildMessage(ConstraintViolation violation) {
        return messageSource.getMessage(violation.getMessage(), Locale.getDefault()).orElse(violation.getMessage());
    }
}
