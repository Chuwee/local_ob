package es.onebox.mgmt.validation;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;


public class LanguageIETFValidator implements ConstraintValidator<LanguageIETF, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        String locale = ConverterUtils.toLocale(value);
        if (locale == null || locale.isEmpty()) {
            String message = String.format("language format must be IETF (%s)", value);
            ValidationUtils.addViolation(context, message);
            return false;
        }
        return true;
    }
}
