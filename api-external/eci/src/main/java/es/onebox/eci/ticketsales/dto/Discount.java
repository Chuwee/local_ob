package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Discount implements Serializable {

    @Serial
    private static final long serialVersionUID = -2793103230261164450L;

    @JsonProperty("promotional_action")
    private PromotionalAction promotionalAction;

    public PromotionalAction getPromotionalAction() {
        return promotionalAction;
    }

    public void setPromotionalAction(PromotionalAction promotionalAction) {
        this.promotionalAction = promotionalAction;
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
