package es.onebox.ms.notification.webhooks.dto.promotion;

import es.onebox.ms.notification.webhooks.enums.promotion.PromotionDiscountType;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionDiscountConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionDiscountType type;
    private Double value;
    private Long currencyId;
    private List<PromotionRangeDTO> ranges;

    public PromotionDiscountType getType() {
        return type;
    }

    public void setType(PromotionDiscountType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public List<PromotionRangeDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<PromotionRangeDTO> ranges) {
        this.ranges = ranges;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
