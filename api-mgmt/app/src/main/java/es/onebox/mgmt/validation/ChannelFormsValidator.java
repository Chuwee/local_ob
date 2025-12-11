package es.onebox.mgmt.validation;

import es.onebox.mgmt.channels.forms.dto.UpdateChannelDefaultFormDTO;
import es.onebox.mgmt.validation.annotation.ChannelForms;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;


public class ChannelFormsValidator implements ConstraintValidator<ChannelForms, UpdateChannelDefaultFormDTO> {

    @Override
    public boolean isValid(UpdateChannelDefaultFormDTO value, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(value.getPurchase())) {
            ValidationUtils.addViolation(context, "forms content cannot be empty");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
