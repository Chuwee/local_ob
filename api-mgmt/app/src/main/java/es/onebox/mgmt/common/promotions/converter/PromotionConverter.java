package es.onebox.mgmt.common.promotions.converter;

import es.onebox.mgmt.common.promotions.dto.PromotionLimitDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionMaxLimitDTO;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionLimit;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionMaxLimit;

public class PromotionConverter {

    public static PromotionLimitDTO createPromotionLimit(PromotionLimit inDto) {
        if (inDto == null) {
            return null;
        }
        return new PromotionLimitDTO(inDto.getEnabled(), inDto.getLimit());
    }

    public static PromotionLimit limit(PromotionLimitDTO limit) {
        PromotionLimit inLimit = new PromotionLimit();
        inLimit.setEnabled(limit.getEnabled());
        inLimit.setLimit(limit.getLimit());
        return inLimit;
    }

    public static PromotionMaxLimit maxLimit(PromotionMaxLimitDTO promotionMaxLimit) {
        PromotionMaxLimit inLimit = new PromotionMaxLimit();
        inLimit.setEnabled(promotionMaxLimit.getEnabled());
        inLimit.setLimit(promotionMaxLimit.getLimit());
        inLimit.setCurrent(promotionMaxLimit.getCurrent());
        return inLimit;
    }

    public static PromotionMaxLimitDTO createPromotionMaxLimit(PromotionMaxLimit inDto) {
        if (inDto == null) {
            return null;
        }
        return new PromotionMaxLimitDTO(inDto.getEnabled(), inDto.getLimit(), inDto.getCurrent());
    }
}
