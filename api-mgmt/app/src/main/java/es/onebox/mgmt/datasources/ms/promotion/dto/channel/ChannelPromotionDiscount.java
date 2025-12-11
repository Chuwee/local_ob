package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionDiscountType;
import es.onebox.mgmt.datasources.ms.promotion.dto.AmountCurrency;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionDiscount implements Serializable {

    private static final long serialVersionUID = 6900105671786456825L;

    private ChannelPromotionDiscountType type;
    private Double value;
    private List<AmountCurrency> fixedValues;
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

    public List<AmountCurrency> getFixedValues() {
        return fixedValues;
    }

    public Double getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(Double percentageValue) {
        this.percentageValue = percentageValue;
    }

    public void setFixedValues(List<AmountCurrency> fixedValues) {
        this.fixedValues = fixedValues;
    }
}
