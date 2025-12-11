package es.onebox.mgmt.events.promotiontemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.PromotionDiscountConfigDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionSurchargesDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionTemplateDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionValidityPeriodDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventPromotionTemplateDetailDTO extends PromotionTemplateDTO {

    private static final long serialVersionUID = 1L;

    private PromotionDiscountConfigDTO discount;
    @JsonProperty("usage_limits")
    private PromotionLimitsDTO limits;
    @JsonProperty("validity_period")
    private PromotionValidityPeriodDTO validityPeriod;
    private PromotionSurchargesDTO surcharges;
    @JsonProperty("show_discount_name_ticket")
    private Boolean showDiscountNameticket;
    @JsonProperty("show_ticket_price_without_discount")
    private Boolean showTicketPriceWithoutDiscount;
    @JsonProperty("access_control_restricted")
    private Boolean accesControlRestricted;
    private Boolean combinable;
    private PromotionTemplateCollectiveDTO collective;
    private Boolean favorite;
    private Boolean presale;

    public PromotionDiscountConfigDTO getDiscount() {
        return discount;
    }

    public void setDiscount(PromotionDiscountConfigDTO discount) {
        this.discount = discount;
    }

    public PromotionLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(PromotionLimitsDTO limits) {
        this.limits = limits;
    }

    public PromotionValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PromotionValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public PromotionSurchargesDTO getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(PromotionSurchargesDTO surcharges) {
        this.surcharges = surcharges;
    }

    public Boolean getShowDiscountNameticket() {
        return showDiscountNameticket;
    }

    public void setShowDiscountNameticket(Boolean showDiscountNameticket) {
        this.showDiscountNameticket = showDiscountNameticket;
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

    public PromotionTemplateCollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(PromotionTemplateCollectiveDTO collective) {
        this.collective = collective;
    }

    @Override
    public Boolean getFavorite() {
        return favorite;
    }

    @Override
    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getAccesControlRestricted() {
        return accesControlRestricted;
    }

    public void setAccesControlRestricted(Boolean accesControlRestricted) {
        this.accesControlRestricted = accesControlRestricted;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
    }
}
