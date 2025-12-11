package es.onebox.event.promotions.dto;

import es.onebox.event.promotions.dto.restriction.PromotionRestrictions;
import es.onebox.event.promotions.enums.PromotionStatus;
import es.onebox.event.promotions.enums.PromotionType;

import java.io.Serial;
import java.io.Serializable;

public class EventPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 4549370513592118206L;

    private Long eventId;
    private Long eventPromotionTemplateId;
    private Long promotionTemplateId;
    private String name;
    private Boolean active;
    private Boolean applyChannelSpecificCharges;
    private Boolean applyPromoterSpecificCharges;
    private PromotionStatus status;
    private PromotionType type;
    private PromotionCommElements commElements;
    private PromotionRestrictions restrictions;
    private PromotionPriceVariation priceVariation;
    private Boolean selfManaged;
    private Boolean restrictiveAccess;
    private Boolean blockSecondaryMarketSale;
    private PromotionUsageConditions usageConditions;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventPromotionTemplateId() {
        return eventPromotionTemplateId;
    }

    public void setEventPromotionTemplateId(Long eventPromotionTemplateId) {
        this.eventPromotionTemplateId = eventPromotionTemplateId;
    }

    public Long getPromotionTemplateId() {
        return promotionTemplateId;
    }

    public void setPromotionTemplateId(Long promotionTemplateId) {
        this.promotionTemplateId = promotionTemplateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getApplyChannelSpecificCharges() {
        return applyChannelSpecificCharges;
    }

    public void setApplyChannelSpecificCharges(Boolean applyChannelSpecificCharges) {
        this.applyChannelSpecificCharges = applyChannelSpecificCharges;
    }

    public Boolean getApplyPromoterSpecificCharges() {
        return applyPromoterSpecificCharges;
    }

    public void setApplyPromoterSpecificCharges(Boolean applyPromoterSpecificCharges) {
        this.applyPromoterSpecificCharges = applyPromoterSpecificCharges;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionCommElements getCommElements() {
        return commElements;
    }

    public void setCommElements(PromotionCommElements commElements) {
        this.commElements = commElements;
    }

    public PromotionRestrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(PromotionRestrictions restrictions) {
        this.restrictions = restrictions;
    }

    public PromotionPriceVariation getPriceVariation() {
        return priceVariation;
    }

    public void setPriceVariation(PromotionPriceVariation priceVariation) {
        this.priceVariation = priceVariation;
    }

    public Boolean getSelfManaged() { return selfManaged; }

    public void setSelfManaged(Boolean selfManaged) { this.selfManaged = selfManaged; }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
    }

    public Boolean getBlockSecondaryMarketSale() {
        return blockSecondaryMarketSale;
    }

    public void setBlockSecondaryMarketSale(Boolean blockSecondaryMarketSale) {
        this.blockSecondaryMarketSale = blockSecondaryMarketSale;
    }

    public PromotionUsageConditions getUsageConditions() {
        return usageConditions;
    }

    public void setUsageConditions(PromotionUsageConditions usageConditions) {
        this.usageConditions = usageConditions;
    }
}
