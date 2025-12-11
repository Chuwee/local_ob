package es.onebox.mgmt.channels.promotions.dto;

import es.onebox.mgmt.common.AmountCurrencyDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionAmountDTO implements Serializable {

    private static final long serialVersionUID = -7063686849357287171L;

    private Boolean enabled;
    private Double amount;
    private List<AmountCurrencyDTO> values;

    public ChannelPromotionAmountDTO(){
    }

    public ChannelPromotionAmountDTO(Boolean enabled, Double amount) {
        this.enabled = enabled;
        this.amount = amount;
    }

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

    public List<AmountCurrencyDTO> getValues() {
        return values;
    }

    public void setValues(List<AmountCurrencyDTO> values) {
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
