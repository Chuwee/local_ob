package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.UrlWithPortFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(validatedBy = UrlWithPortFormatValidator.class)
public @interface UrlWithPortFormat {

    String message() default "Invalid url format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}