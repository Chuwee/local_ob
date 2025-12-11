package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Set;

public class UpdateChannelPromotionPriceTypes implements Serializable {

    private static final long serialVersionUID = 849221866533492454L;

    private PromotionTargetType type;
    private Set<Long> priceTypes;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
        this.type = type;
    }

    public Set<Long> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(Set<Long> priceTypes) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
