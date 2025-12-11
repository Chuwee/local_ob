package es.onebox.mgmt.validation.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import es.onebox.mgmt.validation.ImageContentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = ImageContentValidator.class)
public @interface ImageContent {

    String message() default "Invalid image size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}