package es.onebox.mgmt.validation;

import es.onebox.mgmt.common.promotions.dto.PromotionDiscountConfigDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionValidityPeriodDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionDiscountType;
import es.onebox.mgmt.common.promotions.enums.PromotionValidityType;
import es.onebox.mgmt.events.promotions.dto.EventPromotionCollectiveTypeDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionDetailDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.PromotionTemplateCollectiveDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.UpdateEventPromotionTemplateDTO;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.validation.annotation.Promotion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;


public class PromotionValidator implements ConstraintValidator<Promotion, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof UpdateEventPromotionDetailDTO) {
            UpdateEventPromotionDetailDTO detail = (UpdateEventPromotionDetailDTO) value;
            if (detail.getDiscount() != null) {
                PromotionDiscountConfigDTO discount = detail.getDiscount();
                if (!isValidDiscount(discount, detail.getPresale(), context)) {
                    return Boolean.FALSE;
                }
            }
            if (detail.getValidityPeriod() != null) {
                PromotionValidityPeriodDTO validity = detail.getValidityPeriod();
                if (!isValidPromotionPeriod(validity, context)) {
                    return Boolean.FALSE;
                }
            }
            if (detail.getLimits() != null) {
                PromotionLimitDTO packs = detail.getLimits().getPacks();
                PromotionLimitDTO purchaseMin = detail.getLimits().getPurchaseMinLimit();
                if (packs != null && BooleanUtils.isTrue(packs.getEnabled()) && purchaseMin != null
                        && BooleanUtils.isTrue(purchaseMin.getEnabled())) {
                    ValidationUtils.addViolation(context, "purchase_min and ticket_group_min cannot be enabled twice");
                    return Boolean.FALSE;
                }
            }
            if (detail.getCollective() != null) {
                if (!isValidCollective(detail.getCollective(), detail.getPresale(), context)) {
                    return Boolean.FALSE;
                }
            }

        } else if (value instanceof UpdateEventPromotionTemplateDTO)  {
            UpdateEventPromotionTemplateDTO template = (UpdateEventPromotionTemplateDTO) value;
            if (template.getDiscount() != null) {
                PromotionDiscountConfigDTO discount = template.getDiscount();
                if (!isValidDiscount(discount, template.getPresale(), context)) {
                    return Boolean.FALSE;
                }
            }
            if (template.getValidityPeriod() != null) {
                PromotionValidityPeriodDTO validity = template.getValidityPeriod();
                if (!isValidPromotionPeriod(validity, context)) {
                    return Boolean.FALSE;
                }
            }
            if (template.getCollective() != null) {
                if (!isValidCollective(template.getCollective(), template.getPresale(), context)) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    private static boolean isValidCollective(PromotionTemplateCollectiveDTO template, Boolean presale, ConstraintValidatorContext context) {
        if (EventPromotionCollectiveTypeDTO.NONE.equals(template.getType()) && BooleanUtils.isTrue(presale)) {
            ValidationUtils.addViolation(context, "collective.type NONE is not allowed for presale promotions");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private static boolean isValidDiscount(PromotionDiscountConfigDTO discount, Boolean presale, ConstraintValidatorContext context) {
        if (!PromotionDiscountType.BASE_PRICE.equals(discount.getType())) {
            if (discount.getValue() == null) {
                ValidationUtils.addViolation(context, "Discount value must be informed");
                return Boolean.FALSE;
            }
            if (PromotionDiscountType.PERCENTAGE.equals(discount.getType()) && (discount.getValue() > 100D || discount.getValue() < 0D)) {
                ValidationUtils.addViolation(context, "Discount value must not be greater than 100% and less than 0%");
                return Boolean.FALSE;
            }
        } else if (PromotionDiscountType.BASE_PRICE.equals(discount.getType())
                && CollectionUtils.isEmpty(discount.getRanges())) {
            ValidationUtils.addViolation(context, "At least one range must be informed");
            return Boolean.FALSE;
        } else if (PromotionDiscountType.FIXED.equals(discount.getType()) && NumberUtils.DOUBLE_ZERO.equals(discount.getValue())) {
            ValidationUtils.addViolation(context, "Discount value cant be zero");
            return Boolean.FALSE;
        } else if (PromotionDiscountType.NO_DISCOUNT.equals(discount.getType()) && BooleanUtils.isFalse(presale)) {
            ValidationUtils.addViolation(context, ApiMgmtPromotionErrorCode.EVENT_PROMOTION_INVALID_DISCOUNT_TYPE.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private static boolean isValidPromotionPeriod(PromotionValidityPeriodDTO validity, ConstraintValidatorContext context) {
        if (PromotionValidityType.PERIOD.equals(validity.getType())
                && (validity.getEndDate() == null || validity.getStartDate() == null)) {
            ValidationUtils.addViolation(context, "Period dates must be informed");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
