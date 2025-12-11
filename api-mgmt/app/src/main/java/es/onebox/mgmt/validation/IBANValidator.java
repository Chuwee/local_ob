package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.IBAN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class IBANValidator implements ConstraintValidator<IBAN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        org.apache.commons.validator.routines.IBANValidator ibanValidator = new org.apache.commons.validator.routines.IBANValidator();
        return ibanValidator.isValid(value);
    }

}
