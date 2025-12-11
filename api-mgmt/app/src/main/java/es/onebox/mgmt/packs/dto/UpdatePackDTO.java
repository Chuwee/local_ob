package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdatePackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Boolean active;
    @JsonProperty("pack_period")
    private PackPeriodDTO packPeriod;
    private PackPricingDTO pricing;
    @JsonProperty("ui_settings")
    private PackUISettings packUISettings;
    private PackSettingsDTO settings;
    @JsonProperty("unified_price")
    private Boolean unifiedPrice;
    @JsonProperty("tax_id")
    private Long taxId;
    private Boolean suggested;

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

    public PackUISettings getPackUISettings() {
        return packUISettings;
    }

    public void setPackUISettings(PackUISettings packUISettings) {
        this.packUISettings = packUISettings;
    }

    public PackSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(PackSettingsDTO settings) {
        this.settings = settings;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Boolean getUnifiedPrice() {
        return unifiedPrice;
    }

    public void setUnifiedPrice(Boolean unifiedPrice) {
        this.unifiedPrice = unifiedPrice;
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
