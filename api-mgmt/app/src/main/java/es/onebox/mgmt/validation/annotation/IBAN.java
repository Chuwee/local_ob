package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.IBANValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = IBANValidator.class)
public @interface IBAN {

    String message() default "Invalid iban";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}