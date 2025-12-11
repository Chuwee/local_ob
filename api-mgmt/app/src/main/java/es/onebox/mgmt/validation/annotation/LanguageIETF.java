package es.onebox.mgmt.validation.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import es.onebox.mgmt.validation.LanguageIETFKeyMapValidator;
import es.onebox.mgmt.validation.LanguageIETFValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {LanguageIETFValidator.class, LanguageIETFKeyMapValidator.class})
public @interface LanguageIETF {

    String message() default "language format must be IETF";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
