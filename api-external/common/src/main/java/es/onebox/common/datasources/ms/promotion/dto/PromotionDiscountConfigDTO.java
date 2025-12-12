package es.onebox.common.datasources.ms.promotion.dto;

import es.onebox.common.datasources.ms.promotion.enums.PromotionDiscountType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PromotionDiscountConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PromotionDiscountType type;
    private Double value;

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
