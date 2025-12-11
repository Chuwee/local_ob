package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.EmailList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;

public class EmailListValidator implements ConstraintValidator<EmailList, String> {

    private static final String SEPARATOR = "(,\\s*)";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        EmailValidator emailValidator = EmailValidator.getInstance();

        return Arrays.stream(value.split(SEPARATOR))
                .map(emailValidator::isValid)
                .allMatch(elem -> elem.equals(Boolean.TRUE));
    }
}
