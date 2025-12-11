package es.onebox.event.packs.dto;

import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackRangeType;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.enums.PackType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -181826856077863128L;

    private Long id;
    private String name;
    private PackType type;
    private PackSubtype subtype;
    private Long entityId;
    private String entityName;
    private Boolean soldOut;
    private Boolean forSale;
    private Boolean onSale;
    private Long promotionId;
    private PackPricingType pricingType;
    private PackRangeType packRangeType;
    private ZonedDateTime customStartSaleDate;
    private ZonedDateTime customEndSaleDate;
    private Boolean unifiedPrice;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showMainVenue;
    private Boolean showMainDate;
    private Boolean active;
    private Double priceIncrement;

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

    public PackType getType() {
        return type;
    }

    public void setType(PackType type) {
        this.type = type;
    }

    public PackSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(PackSubtype subtype) {
        this.subtype = subtype;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public PackRangeType getPackRangeType() {
        return packRangeType;
    }

    public void setPackRangeType(PackRangeType packRangeType) {
        this.packRangeType = packRangeType;
    }

    public ZonedDateTime getCustomStartSaleDate() {
        return customStartSaleDate;
    }

    public void setCustomStartSaleDate(ZonedDateTime customStartSaleDate) {
        this.customStartSaleDate = customStartSaleDate;
    }

    public ZonedDateTime getCustomEndSaleDate() {
        return customEndSaleDate;
    }

    public void setCustomEndSaleDate(ZonedDateTime customEndSaleDate) {
        this.customEndSaleDate = customEndSaleDate;
    }

    public Boolean getUnifiedPrice() {
        return unifiedPrice;
    }

    public void setUnifiedPrice(Boolean unifiedPrice) {
        this.unifiedPrice = unifiedPrice;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }

    public Boolean getShowMainVenue() {
        return showMainVenue;
    }

    public void setShowMainVenue(Boolean showMainVenue) {
        this.showMainVenue = showMainVenue;
    }

    public Boolean getShowMainDate() {
        return showMainDate;
    }

    public void setShowMainDate(Boolean showMainDate) {
        this.showMainDate = showMainDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(Double priceIncrement) {
        this.priceIncrement = priceIncrement;
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
