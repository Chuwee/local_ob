package es.onebox.mgmt.validation.annotation;

import es.onebox.mgmt.validation.ImagePositionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = ImagePositionValidator.class)
public @interface ImageWithPosition {

    String message() default "Invalid image size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
