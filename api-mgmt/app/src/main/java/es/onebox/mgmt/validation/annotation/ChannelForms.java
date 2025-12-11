package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.ChannelFormsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = ChannelFormsValidator.class)
public @interface ChannelForms {

    String message() default "Invalid channel forms update";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}