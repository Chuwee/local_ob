package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.EventGroupPricePolicy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EventSettingsGroupsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;

    @JsonProperty("price_policy")
    private EventGroupPricePolicy pricePolicy;

    @JsonProperty("companions_payment")
    private Boolean companionsPayment;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public EventGroupPricePolicy getPricePolicy() {
        return pricePolicy;
    }

    public void setPricePolicy(EventGroupPricePolicy pricePolicy) {
        this.pricePolicy = pricePolicy;
    }

    public Boolean getCompanionsPayment() {
        return companionsPayment;
    }

    public void setCompanionsPayment(Boolean companionsPayment) {
        this.companionsPayment = companionsPayment;
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
