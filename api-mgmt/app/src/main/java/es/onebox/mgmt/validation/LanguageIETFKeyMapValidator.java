package es.onebox.mgmt.validation;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

public class LanguageIETFKeyMapValidator implements ConstraintValidator<LanguageIETF, Map<String, String>> {

    @Override
    public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
        if (MapUtils.isEmpty(value)) {
            return true;
        }
        for (String language: value.keySet()) {
            String locale = ConverterUtils.toLocale(language);
            if (locale == null || locale.isEmpty()) {
                String message = String.format("language format must be IETF (%s)", language);
                ValidationUtils.addViolation(context, message);
                return false;
            }
        }
        return true;
    }
}
