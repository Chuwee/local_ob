package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.RelativeTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.TYPE,})
@Constraint(validatedBy = RelativeTimeValidator.class)
public @interface RelativeTime {

    String message() default "Invalid relative time value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
