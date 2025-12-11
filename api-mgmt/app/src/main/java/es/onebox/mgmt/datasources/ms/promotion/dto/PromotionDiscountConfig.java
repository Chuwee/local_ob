package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PromotionDiscountConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionDiscountType type;
    private Double value;
    private List<PromotionRange> ranges;
    private Long currencyId;

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

    public List<PromotionRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<PromotionRange> ranges) {
        this.ranges = ranges;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
