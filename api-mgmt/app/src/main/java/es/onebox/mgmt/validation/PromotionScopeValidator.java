package es.onebox.mgmt.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.validation.annotation.PromotionScope;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class PromotionScopeValidator implements ConstraintValidator<PromotionScope, PromotionTarget> {

    @Override
    public boolean isValid(PromotionTarget value, ConstraintValidatorContext context) {
        if (PromotionTargetType.RESTRICTED.equals(value.getType()) && CollectionUtils.isEmpty(value.getData())) {
            String name = getPropertyName(value);
            ValidationUtils.addViolation(context,
                    (String.format(ApiMgmtPromotionErrorCode.EVENT_PROMOTION_EMPTY_TARGET.getMessage(),
                            name)));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private static String getPropertyName(PromotionTarget value) {
        PropertyDescriptor pd;
        pd = BeanUtils.getPropertyDescriptor(value.getClass(), "data");
        Method getter = pd.getReadMethod();
        JsonProperty property = getter.getAnnotation(JsonProperty.class);
        return property.value();
    }
}
