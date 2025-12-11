package es.onebox.event.promotions.dto;

import es.onebox.event.promotions.enums.PromotionPriceVariationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PromotionPriceVariation implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionPriceVariationType type;
    private List<PromotionPriceVariationValue> value;

    public PromotionPriceVariationType getType() {
        return type;
    }

    public void setType(PromotionPriceVariationType type) {
        this.type = type;
    }

    public List<PromotionPriceVariationValue> getValue() {
        return value;
    }

    public void setValue(List<PromotionPriceVariationValue> value) {
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
