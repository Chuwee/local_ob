package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionPriceTypes implements Serializable {

    private static final long serialVersionUID = 5726960586181741137L;

    private PromotionTargetType type;
    private List<ChannelPromotionPriceType> priceTypes;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
        this.type = type;
    }

    public List<ChannelPromotionPriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<ChannelPromotionPriceType> priceTypes) {
        this.priceTypes = priceTypes;
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
