package es.onebox.mgmt.validation;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.annotation.ImageContent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

public class ImageContentValidator implements ConstraintValidator<ImageContent, CommunicationElementImageDTO<?>> {

    @Override
    public boolean isValid(CommunicationElementImageDTO<?> value, ConstraintValidatorContext context) {
        if (value.getType() == null) {
            ValidationUtils.addViolation(context, ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_TYPE.getMessage());
            return false;
        }
        if (StringUtils.isBlank(value.getImageBinary())) {
            ValidationUtils.addViolation(context,
                    (String.format(ApiMgmtErrorCode.IMAGE_INVALID_EMPTY.getMessage(), value.getType().toString())));
            return false;
        }
        if (!Base64.isArrayByteBase64(value.getImageBinary().getBytes())) {
            ValidationUtils.addViolation(context,
                    (String.format(ApiMgmtErrorCode.IMAGE_INVALID_ENCODING.getMessage(), value.getType().toString())));
            return false;
        }
        if (value.getType() instanceof SizeConstrained) {
            SizeConstrained type = (SizeConstrained) value.getType();
            FileUtils.checkImage(value.getImageBinary(), type, value.getType().toString());
            return true;
        }
        throw new IllegalArgumentException("Type must be size constrained");
    }

}
