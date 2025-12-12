package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Sales implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Sale promotion;
    private Sale discount;
    private Sale automatic;

    public Sale getPromotion() {
        return promotion;
    }

    public void setPromotion(Sale promotion) {
        this.promotion = promotion;
    }

    public Sale getDiscount() {
        return discount;
    }

    public void setDiscount(Sale discount) {
        this.discount = discount;
    }

    public Sale getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Sale automatic) {
        this.automatic = automatic;
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
