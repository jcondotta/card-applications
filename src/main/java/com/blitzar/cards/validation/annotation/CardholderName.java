package com.blitzar.cards.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank
@Size(max = 21)
@Target(value = { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(value = RUNTIME)
@Constraint(validatedBy = {})
public @interface CardholderName {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
