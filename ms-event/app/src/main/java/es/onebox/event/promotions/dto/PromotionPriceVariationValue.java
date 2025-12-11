package es.onebox.event.promotions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionPriceVariationValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double from;
    private Double value;

    public PromotionPriceVariationValue() {
        super();
    }

    public PromotionPriceVariationValue(Double from, Double value) {
        this();
        this.from = from;
        this.value = value;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
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
