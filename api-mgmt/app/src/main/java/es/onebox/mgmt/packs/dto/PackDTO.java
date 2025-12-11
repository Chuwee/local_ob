package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    private Long id;
    private String name;
    private Boolean active;
    private PackTypeDTO type;
    private IdNameDTO entity;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("promotion")
    private PackPromotionDTO promotion;
    @JsonProperty("pack_period")
    private PackPeriodDTO packPeriod;
    private PackPricingDTO pricing;
    @JsonProperty("unified_price")
    private Boolean unifiedPrice;
    @JsonProperty("has_sales")
    private Boolean hasSales;
    @JsonProperty("ui_settings")
    private PackUISettings packUISettings;
    private Boolean suggested;

    public PackDTO() {
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

    public PackTypeDTO getType() {
        return type;
    }

    public void setType(PackTypeDTO type) {
        this.type = type;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public PackPromotionDTO getPromotion() {
        return promotion;
    }

    public void setPromotion(PackPromotionDTO promotion) {
        this.promotion = promotion;
    }

    public PackPeriodDTO getPackPeriod() {
        return packPeriod;
    }

    public void setPackPeriod(PackPeriodDTO packPeriod) {
        this.packPeriod = packPeriod;
    }

    public PackPricingDTO getPricing() {
        return pricing;
    }

    public void setPricing(PackPricingDTO pricing) {
        this.pricing = pricing;
    }

    public Boolean getUnifiedPrice() {
        return unifiedPrice;
    }

    public void setUnifiedPrice(Boolean unifiedPrice) {
        this.unifiedPrice = unifiedPrice;
    }

    public Boolean getHasSales() {
        return hasSales;
    }

    public void setHasSales(Boolean hasSales) {
        this.hasSales = hasSales;
    }

    public PackUISettings getPackUISettings() {
        return packUISettings;
    }

    public void setPackUISettings(PackUISettings packUISettings) {
        this.packUISettings = packUISettings;
    }

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
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
