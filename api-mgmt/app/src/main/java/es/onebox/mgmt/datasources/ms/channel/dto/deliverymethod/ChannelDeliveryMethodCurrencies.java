package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelDeliveryMethodCurrencies implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double cost;
    private Long currencyId;

    public ChannelDeliveryMethodCurrencies() {
    }

    public ChannelDeliveryMethodCurrencies(Double cost, Long currencyId) {
        this.cost = cost;
        this.currencyId = currencyId;
    }

    public Double getCost() { return cost;  }

    public void setCost(Double cost) { this.cost = cost; }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
