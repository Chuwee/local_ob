package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionDiscountType;
import es.onebox.mgmt.common.AmountCurrencyDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionDiscountDTO implements Serializable {

    private static final long serialVersionUID = -1996584014292786874L;

    @JsonProperty("type")
    private ChannelPromotionDiscountType type;
    @JsonProperty("value")
    private Double value;
    @JsonProperty("fixed_values")
    private List<AmountCurrencyDTO> fixedValues;
    @JsonProperty("percentage_value")
    private Double percentageValue;

    public ChannelPromotionDiscountType getType() {
        return type;
    }

    public void setType(ChannelPromotionDiscountType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<AmountCurrencyDTO> getFixedValues() {
        return fixedValues;
    }

    public void setFixedValues(List<AmountCurrencyDTO> fixedValues) {
        this.fixedValues = fixedValues;
    }

    public Double getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(Double percentageValue) {
        this.percentageValue = percentageValue;
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
