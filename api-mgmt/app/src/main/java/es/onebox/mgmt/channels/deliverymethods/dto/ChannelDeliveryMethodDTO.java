package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;


public class ChannelDeliveryMethodDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be null")
    private DeliveryMethodDTO type;
    private ChannelDeliveryMethodStatusDTO status;
    @JsonProperty("default")
    private Boolean defaultMethod;
    private List<ChannelDeliveryMethodCurrenciesDTO> currencies;
    private List<TaxInfoDTO> taxes;

    public DeliveryMethodDTO getType() {
        return type;
    }

    public void setType(DeliveryMethodDTO type) {
        this.type = type;
    }

    public ChannelDeliveryMethodStatusDTO getStatus() {
        return status;
    }

    public void setStatus(ChannelDeliveryMethodStatusDTO status) {
        this.status = status;
    }

    public Boolean getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(Boolean defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public List<ChannelDeliveryMethodCurrenciesDTO> getCurrencies() { return currencies; }

    public void setCurrencies(List<ChannelDeliveryMethodCurrenciesDTO> currencies) { this.currencies = currencies; }

    public List<TaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfoDTO> taxes) {
        this.taxes = taxes;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public void setCost(double v) {
    }
}
