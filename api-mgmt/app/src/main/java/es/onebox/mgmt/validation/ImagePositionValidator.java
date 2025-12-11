package es.onebox.mgmt.validation;

import es.onebox.mgmt.common.ImageSortable;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.dto.CommunicationElementImageWithPositionDTO;
import es.onebox.mgmt.validation.annotation.ImageWithPosition;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ImagePositionValidator implements ConstraintValidator<ImageWithPosition, CommunicationElementImageWithPositionDTO<?>> {

    @Override
    public boolean isValid(CommunicationElementImageWithPositionDTO<?> value, ConstraintValidatorContext context) {
        if (value.getType() instanceof ImageSortable) {
            ImageSortable type = (ImageSortable) value.getType();
            if (Boolean.TRUE.equals(type.getPositionRequired()) && value.getPosition() == null) {
                ValidationUtils.addViolation(context,
                        (String.format(ApiMgmtErrorCode.IMAGE_POSITION_REQUIRED.getMessage(), value.getType().toString())));
                return false;
            }
            return true;
        }
        throw new IllegalArgumentException("Type must be size constrained");
    }

}
