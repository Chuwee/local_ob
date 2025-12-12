package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 4933484239646273236L;

    private Promotion automatic;
    private Promotion promotion;
    private Promotion discount;

    public Promotion getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Promotion automatic) {
        this.automatic = automatic;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public Promotion getDiscount() {
        return discount;
    }

    public void setDiscount(Promotion discount) {
        this.discount = discount;
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
