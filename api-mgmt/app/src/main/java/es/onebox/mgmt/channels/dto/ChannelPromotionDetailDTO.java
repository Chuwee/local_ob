package es.onebox.mgmt.channels.dto;

import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionCollectiveDetailDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDiscountDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotionDetailDTO extends ChannelPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3511653303789651713L;

    private Boolean combinable;
    private ChannelPromotionCollectiveDetailDTO collective;
    private ChannelPromotionDiscountDTO discount;

    public Boolean getCombinable() {
        return combinable;
    }
    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
    }

    public ChannelPromotionCollectiveDetailDTO getCollective() {
        return collective;
    }

    public void setCollective(ChannelPromotionCollectiveDetailDTO collective) {
        this.collective = collective;
    }

    @Override
    public ChannelPromotionDiscountDTO getDiscount() {
        return discount;
    }

    @Override
    public void setDiscount(ChannelPromotionDiscountDTO discount) {
        this.discount = discount;
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
