package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionSubtypeDTO;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionType;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private PromotionStatus status;
    @JsonProperty("block_secondary_market_sale")
    private Boolean blockSecondaryMarketSale;
    private ChannelPromotionType type;
    private ChannelPromotionSubtypeDTO subtype;
    @JsonProperty("validity_period")
    private ChannelPromotionPeriodDTO validityPeriod;
    @JsonProperty("alternative_surcharges")
    private AlternativeSurchargesDTO alternativeSurcharges;
    private ChannelPromotionDiscountDTO discount;
    @JsonProperty("usage_limits")
    private ChannelPromotionLimitsDTO limits;
    private ChannelPromotionPacksDTO packs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public ChannelPromotionSubtypeDTO getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelPromotionSubtypeDTO subtype) {
        this.subtype = subtype;
    }

    public ChannelPromotionPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ChannelPromotionPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ChannelPromotionDiscountDTO getDiscount() {
        return discount;
    }

    public void setDiscount(ChannelPromotionDiscountDTO discount) {
        this.discount = discount;
    }

    public ChannelPromotionLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(ChannelPromotionLimitsDTO limits) {
        this.limits = limits;
    }

    public ChannelPromotionPacksDTO getPacks() {
        return packs;
    }

    public void setPacks(ChannelPromotionPacksDTO packs) {
        this.packs = packs;
    }

    public AlternativeSurchargesDTO getAlternativeSurcharges() {
        return alternativeSurcharges;
    }

    public void setAlternativeSurcharges(AlternativeSurchargesDTO alternativeSurcharges) {
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
