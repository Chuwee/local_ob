package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.RelativeTime;
import es.onebox.mgmt.vouchers.dto.RelativeTimeDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class RelativeTimeValidator implements ConstraintValidator<RelativeTime, RelativeTimeDTO> {

    @Override
    public boolean isValid(RelativeTimeDTO value, ConstraintValidatorContext context) {
        if (value.getType() == null) {
            return false;
        }
        switch (value.getType()) {
            case DISABLED:
                if (value.getRelativeAmount() != null || value.getTimePeriod() != null || value.getFixedDate() != null) {
                    return false;
                }
                break;
            case FIXED:
                if (value.getRelativeAmount() != null || value.getTimePeriod() != null || value.getFixedDate() == null) {
                    return false;
                }
                break;
            case RELATIVE:
                if (value.getRelativeAmount() == null || value.getTimePeriod() == null || value.getFixedDate() != null) {
                    return false;
                }
        }

        return true;
    }
}
