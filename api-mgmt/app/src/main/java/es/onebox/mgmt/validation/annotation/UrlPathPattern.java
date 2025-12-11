package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.UrlPathPatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = UrlPathPatternValidator.class)
public @interface UrlPathPattern {

    String message() default "Invalid url pattern format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
