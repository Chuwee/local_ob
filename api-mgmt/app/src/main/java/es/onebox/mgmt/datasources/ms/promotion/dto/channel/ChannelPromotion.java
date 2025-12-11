package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionSubtype;
import es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionType;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PromotionStatus status;
    private ChannelPromotionType type;
    private ChannelPromotionSubtype subtype;
    private ChannelPromotionPeriod validityPeriod;

    private AlternativeSurcharges alternativeSurcharges;
    private Boolean blockSecondaryMarketSale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public ChannelPromotionType getType() {
        return type;
    }

    public void setType(ChannelPromotionType type) {
        this.type = type;
    }

    public ChannelPromotionSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelPromotionSubtype subtype) {
        this.subtype = subtype;
    }

    public ChannelPromotionPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ChannelPromotionPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public AlternativeSurcharges getAlternativeSurcharges() {
        return alternativeSurcharges;
    }

    public void setAlternativeSurcharges(AlternativeSurcharges alternativeSurcharges) {
        this.alternativeSurcharges = alternativeSurcharges;
    }

    public Boolean getBlockSecondaryMarketSale() {
        return blockSecondaryMarketSale;
    }

    public void setBlockSecondaryMarketSale(Boolean blockSecondaryMarketSale) {
        this.blockSecondaryMarketSale = blockSecondaryMarketSale;
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
