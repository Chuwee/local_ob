package es.onebox.mgmt.datasources.ms.event.dto.packs;

import es.onebox.mgmt.datasources.ms.channel.enums.PackPricingType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackRangeType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Pack implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    private Long id;
    private String name;
    private Boolean active;
    private PackType type;
    private PackSubtype subtype;
    private Long entityId;
    private String entityName;
    private Long channelId;
    private Long promotionId;
    private PackRangeType packRangeType;
    private PackPricingType pricingType;
    private Double priceIncrement;
    private ZonedDateTime customStartSaleDate;
    private ZonedDateTime customEndSaleDate;
    private Boolean unifiedPrice;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showMainVenue;
    private Boolean showMainDate;
    private Boolean suggested;
    private Boolean onSaleForLoggedUsers;

    public Pack() {
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public PackRangeType getPackRangeType() {
        return packRangeType;
    }

    public void setPackRangeType(PackRangeType packRangeType) {
        this.packRangeType = packRangeType;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public Double getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(Double priceIncrement) {
        this.priceIncrement = priceIncrement;
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

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
    }

    public Boolean getOnSaleForLoggedUsers() {
        return onSaleForLoggedUsers;
    }

    public void setOnSaleForLoggedUsers(Boolean onSaleForLoggedUsers) {
        this.onSaleForLoggedUsers = onSaleForLoggedUsers;
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
