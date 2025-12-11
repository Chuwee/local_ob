package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventPromotionDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = -1175872887506271172L;

    private String name;
    private PromotionActivationStatus status;
    private PromotionDiscountConfig discount;
    private PromotionLimits limits;
    private PromotionPeriod validityPeriod;
    private Boolean accesControlRestricted;
    private Boolean showTicketDiscountName;
    private Boolean showTicketPriceWithouDiscount;
    private Boolean combinable;
    private Boolean includePromoterSurcharges;
    private Boolean includeChannelSurcharges;
    private PromotionTemplateCollective collective;
    private Boolean favorite;
    private Boolean presale;
    private Boolean blockSecondaryMarketSale;
    private UpdatePromotionConditions conditions;

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

    public PromotionDiscountConfig getDiscount() {
        return discount;
    }

    public void setDiscount(PromotionDiscountConfig discount) {
        this.discount = discount;
    }

    public Boolean getAccesControlRestricted() {
        return accesControlRestricted;
    }

    public void setAccesControlRestricted(Boolean accesControlRestricted) {
        this.accesControlRestricted = accesControlRestricted;
    }

    public PromotionPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PromotionPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Boolean getShowTicketDiscountName() {
        return showTicketDiscountName;
    }

    public void setShowTicketDiscountName(Boolean showTicketDiscountName) {
        this.showTicketDiscountName = showTicketDiscountName;
    }

    public Boolean getShowTicketPriceWithouDiscount() {
        return showTicketPriceWithouDiscount;
    }

    public void setShowTicketPriceWithouDiscount(Boolean showTicketPriceWithouDiscount) {
        this.showTicketPriceWithouDiscount = showTicketPriceWithouDiscount;
    }

    public Boolean getCombinable() {
        return combinable;
    }

    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
    }

    public Boolean getIncludePromoterSurcharges() {
        return includePromoterSurcharges;
    }

    public void setIncludePromoterSurcharges(Boolean includePromoterSurcharges) {
        this.includePromoterSurcharges = includePromoterSurcharges;
    }

    public Boolean getIncludeChannelSurcharges() {
        return includeChannelSurcharges;
    }

    public void setIncludeChannelSurcharges(Boolean includeChannelSurcharges) {
        this.includeChannelSurcharges = includeChannelSurcharges;
    }

    public PromotionLimits getLimits() {
        return limits;
    }

    public void setLimits(PromotionLimits limits) {
        this.limits = limits;
    }

    public PromotionTemplateCollective getCollective() {
        return collective;
    }

    public void setCollective(PromotionTemplateCollective collective) {
        this.collective = collective;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }


    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
    }

    public Boolean getBlockSecondaryMarketSale() {
        return blockSecondaryMarketSale;
    }

    public void setBlockSecondaryMarketSale(Boolean blockSecondaryMarketSale) {
        this.blockSecondaryMarketSale = blockSecondaryMarketSale;
    }

    public UpdatePromotionConditions getConditions() {
        return conditions;
    }

    public void setConditions(UpdatePromotionConditions conditions) {
        this.conditions = conditions;
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
