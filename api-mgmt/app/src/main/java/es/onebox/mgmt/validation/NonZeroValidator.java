package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.NonZero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NonZeroValidator implements ConstraintValidator<NonZero, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(Integer.valueOf(0).equals(value)){
            ValidationUtils.addViolation(context, "this value can't be zero");
            return false;
        }
        return true;
    }
}
