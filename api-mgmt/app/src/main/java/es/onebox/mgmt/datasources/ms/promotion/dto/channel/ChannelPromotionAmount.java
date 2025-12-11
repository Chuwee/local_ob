package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.promotion.dto.AmountCurrency;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionAmount implements Serializable {

    private static final long serialVersionUID = 6900105671786456825L;

    private Boolean enabled;
    private Double amount;
    private List<AmountCurrency> values;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<AmountCurrency> getValues() {
        return values;
    }

    public void setValues(List<AmountCurrency> values) {
        this.values = values;
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
