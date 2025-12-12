package es.onebox.common.datasources.ms.promotion.dto;

import es.onebox.common.datasources.ms.promotion.enums.PromotionActivationStatus;
import es.onebox.common.datasources.ms.promotion.enums.PromotionType;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class PromotionDetailDTO extends IdNameDTO {


    private static final long serialVersionUID = 2L;


    private PromotionType type;
    private PromotionActivationStatus status;
    private PromotionDiscountConfigDTO discount;
    private PromotionLimitsDTO limits;
    private PromotionPeriodDTO validityPeriod;
    private Boolean accesControlRestricted;
    private Boolean showTicketDiscountName;
    private Boolean showTicketPriceWithoutDiscount;
    private Boolean combinable;
    private Boolean includePromoterSurcharges;
    private Boolean includeChannelSurcharges;
    private PromotionCollectiveDTO collective;
    private Boolean presale;

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionActivationStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionActivationStatus status) {
        this.status = status;
    }

    public PromotionDiscountConfigDTO getDiscount() {
        return discount;
    }

    public void setDiscount(PromotionDiscountConfigDTO discount) {
        this.discount = discount;
    }

    public Boolean getAccesControlRestricted() {
        return accesControlRestricted;
    }

    public void setAccesControlRestricted(Boolean accesControlRestricted) {
        this.accesControlRestricted = accesControlRestricted;
    }

    public PromotionPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PromotionPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Boolean getShowTicketDiscountName() {
        return showTicketDiscountName;
    }

    public void setShowTicketDiscountName(Boolean showTicketDiscountName) {
        this.showTicketDiscountName = showTicketDiscountName;
    }

    public Boolean getShowTicketPriceWithoutDiscount() {
        return showTicketPriceWithoutDiscount;
    }

    public void setShowTicketPriceWithoutDiscount(Boolean showTicketPriceWithoutDiscount) {
        this.showTicketPriceWithoutDiscount = showTicketPriceWithoutDiscount;
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

    public PromotionLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(PromotionLimitsDTO limits) {
        this.limits = limits;
    }

    public PromotionCollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(PromotionCollectiveDTO collective) {
        this.collective = collective;
    }

    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
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
