package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.EmailListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE, FIELD })
@Constraint(validatedBy = EmailListValidator.class)
public @interface EmailList {

    String message() default "emails format is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
