package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CreateSeasonTicketRequestDTO implements Serializable {

    private static final long serialVersionUID = 200898508229338202L;

    private String name;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("producer_id")
    private Long producerId;

    @JsonProperty("category_id")
    private Integer categoryId;

    @JsonProperty("venue_config_id")
    private Long venueConfigId;

    @JsonProperty("charges_tax_id")
    private Long chargesTaxId;

    @JsonProperty("tax_id")
    private Long taxId;

    @JsonProperty("custom_category_id")
    private Integer customCategoryId;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    @JsonProperty("automatic_taxes")
    private Boolean automaticTaxes;

    public Long getChargesTaxId() {
        return chargesTaxId;
    }

    public void setChargesTaxId(Long chargesTaxId) {
        this.chargesTaxId = chargesTaxId;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurrencyCode() { return currencyCode; }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Boolean getAutomaticTaxes() {
        return automaticTaxes;
    }

    public void setAutomaticTaxes(Boolean automaticTaxes) {
        this.automaticTaxes = automaticTaxes;
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
