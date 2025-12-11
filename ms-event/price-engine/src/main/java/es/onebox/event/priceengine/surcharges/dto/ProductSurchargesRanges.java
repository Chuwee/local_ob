package es.onebox.event.priceengine.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductSurchargesRanges implements Serializable {

    @Serial
    private static final long serialVersionUID = 2536733264382250723L;

    private SurchargeRanges promoter;

    public SurchargeRanges getPromoter() {
        return promoter;
    }

    public void setPromoter(SurchargeRanges promoter) {
        this.promoter = promoter;
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
