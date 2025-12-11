package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;


import es.onebox.mgmt.datasources.ms.channel.dto.taxes.TaxInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelDeliveryMethod implements Serializable {
    private static final long serialVersionUID = 1L;

    private DeliveryMethod type;
    private Double cost;
    private DeliveryMethodStatus status;
    private Boolean defaultMethod;
    private List<ChannelDeliveryMethodCurrencies> currencies;
    private List<TaxInfo> taxes;


    public DeliveryMethod getType() {
        return type;
    }

    public void setType(DeliveryMethod type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public DeliveryMethodStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryMethodStatus status) {
        this.status = status;
    }

    public Boolean getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(Boolean defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public List<ChannelDeliveryMethodCurrencies> getCurrencies() { return currencies; }

    public void setCurrencies(List<ChannelDeliveryMethodCurrencies> currencies) { this.currencies = currencies; }

    public List<TaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfo> taxes) {
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
}
