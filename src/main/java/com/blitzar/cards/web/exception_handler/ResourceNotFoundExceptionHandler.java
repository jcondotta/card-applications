package com.blitzar.cards.web.exception_handler;

import com.blitzar.cards.exception.ResourceNotFoundException;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Locale;

@Produces
@Singleton
@Requires(classes = { ResourceNotFoundException.class })
public class ResourceNotFoundExceptionHandler implements ExceptionHandler<ResourceNotFoundException, HttpResponse<?>> {

    private final ErrorResponseProcessor<?> errorResponseProcessor;
    private final MessageSource messageSource;

    @Inject
    public ResourceNotFoundExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        this.messageSource = messageSource;
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    @Status(value = HttpStatus.NOT_FOUND)
    public HttpResponse<?> handle(HttpRequest request, ResourceNotFoundException e) {
        var exceptionMessage =  messageSource.getMessage(e.getMessage(), Locale.getDefault()).orElse(e.getMessage());
        return errorResponseProcessor.processResponse(ErrorContext.builder(request)
                .cause(e)
                .errorMessage(exceptionMessage)
                .build(), HttpResponse.notFound());
    }
}