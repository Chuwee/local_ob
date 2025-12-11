package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

@Valid
public class UpdateChannelPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8451117416260512825L;

    @Size(min = 1, max = 40, message = "name must have between 1 and 40 characters")
    private String name;
    private PromotionStatus status;

    @JsonProperty("validity_period")
    private ChannelPromotionPeriodDTO validityPeriod;

    private ChannelPromotionCollectiveDTO collective;

    private ChannelPromotionDiscountDTO discount;

    @JsonProperty("alternative_surcharges")
    private AlternativeSurchargesDTO alternativeSurcharges;

    private Boolean combinable;

    @JsonProperty("usage_limits")
    private ChannelPromotionLimitsDTO limits;

    private ChannelPromotionPacksDTO packs;

    @JsonProperty("block_secondary_market_sale")
    private Boolean blockSecondaryMarketSale;

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

    public ChannelPromotionPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ChannelPromotionPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ChannelPromotionCollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(ChannelPromotionCollectiveDTO collective) {
        this.collective = collective;
    }

    public Boolean getCombinable() {
        return combinable;
    }

    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
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
