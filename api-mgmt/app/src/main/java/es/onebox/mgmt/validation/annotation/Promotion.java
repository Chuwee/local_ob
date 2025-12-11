package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.PromotionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = PromotionValidator.class)
public @interface Promotion {

    String message() default "Invalid promotion status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}