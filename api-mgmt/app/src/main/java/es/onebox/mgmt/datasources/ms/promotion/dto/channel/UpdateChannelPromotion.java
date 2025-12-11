package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateChannelPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private PromotionActivationStatus status;
    private ChannelPromotionPeriod validityPeriod;
    private ChannelPromotionCollective collective;
    private ChannelPromotionDiscount discount;
    private Boolean combinable;
    private ChannelPromotionLimits limits;
    private ChannelPromotionPacks packs;
    private AlternativeSurcharges alternativeSurcharges;
    private Boolean blockSecondaryMarketSale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionActivationStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionActivationStatus status) {
        this.status = status;
    }

    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
    }

    public Boolean getCombinable() {
        return combinable;
    }

    public ChannelPromotionPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ChannelPromotionPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ChannelPromotionCollective getCollective() {
        return collective;
    }

    public void setCollective(ChannelPromotionCollective collective) {
        this.collective = collective;
    }

    public ChannelPromotionDiscount getDiscount() {
        return discount;
    }

    public void setDiscount(ChannelPromotionDiscount discount) {
        this.discount = discount;
    }

    public ChannelPromotionLimits getLimits() {
        return limits;
    }

    public void setLimits(ChannelPromotionLimits limits) {
        this.limits = limits;
    }

    public ChannelPromotionPacks getPacks() {
        return packs;
    }

    public void setPacks(ChannelPromotionPacks packs) {
        this.packs = packs;
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
