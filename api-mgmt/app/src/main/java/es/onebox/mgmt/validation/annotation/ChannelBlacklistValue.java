package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.ChannelBlacklistValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ChannelBlacklistValueValidator.class)
public @interface ChannelBlacklistValue {

    String message() default "Value format is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}