package es.onebox.mgmt.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Set;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    public static String determineAttributeName(Field field) {
        if (field.isAnnotationPresent(JsonProperty.class)) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            return jsonProperty.value();
        }
        return field.getName();
    }

    public static <T extends Serializable> void validateBody(T in) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(in);
        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
