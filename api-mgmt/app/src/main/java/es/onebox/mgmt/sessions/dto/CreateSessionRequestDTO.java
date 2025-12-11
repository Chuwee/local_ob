package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateSessionRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9059825370121845519L;

    @NotBlank(message = "name is mandatory")
    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;

    @JsonProperty("rates")
    private List<RateDTO> rates;

    @NotNull(message = "venue_template_id is mandatory")
    @Min(value = 1, message = "venue_template_id is mandatory and must be above 0")
    @JsonProperty("venue_template_id")
    private Long venueTemplateId;

    @JsonProperty("automatic_taxes")
    private Boolean automaticTaxes;

    @JsonProperty("tax_ticket_id")
    private Long taxTicketId;

    @JsonProperty("tax_charges_id")
    private Long taxChargesId;

    @NotNull(message = "fields are mandatory")
    private CreateSessionDates dates;

    @JsonProperty("activity_sale_type")
    private SessionSaleType activitySaleType;

    @JsonProperty("pack_config")
    private CreateSessionPackDTO packConfig;

    @JsonProperty("additional_config")
    private CreateSessionAdditionalConfigDTO additionalConfig;

    @Size(message = "Session reference length cannot be above 100 characters", max = 100)
    private String reference;

    @JsonProperty("enable_smart_booking")
    private Boolean enableSmartBooking;

    @Valid
    @JsonProperty("loyalty_points_config")
    private CreateLoyaltyPointsConfigDTO loyaltyPointsConfig;

    @Valid
    @JsonProperty("settings")
    private CreateSessionSettingsDTO settings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Boolean getAutomaticTaxes() { return automaticTaxes; }

    public void setAutomaticTaxes(Boolean automaticTaxes) { this.automaticTaxes = automaticTaxes; }

    public Long getTaxTicketId() {
        return taxTicketId;
    }

    public void setTaxTicketId(Long taxTicketId) {
        this.taxTicketId = taxTicketId;
    }

    public Long getTaxChargesId() {
        return taxChargesId;
    }

    public void setTaxChargesId(Long taxChargesId) {
        this.taxChargesId = taxChargesId;
    }

    public CreateSessionDates getDates() {
        return dates;
    }

    public void setDates(CreateSessionDates dates) {
        this.dates = dates;
    }

    public CreateSessionPackDTO getPackConfig() {
        return packConfig;
    }

    public void setPackConfig(CreateSessionPackDTO packConfig) {
        this.packConfig = packConfig;
    }

    public SessionSaleType getActivitySaleType() {
        return activitySaleType;
    }

    public void setActivitySaleType(SessionSaleType activitySaleType) {
        this.activitySaleType = activitySaleType;
    }

    public CreateSessionAdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(CreateSessionAdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getEnableSmartBooking() {
        return enableSmartBooking;
    }

    public void setEnableSmartBooking(Boolean enableSmartBooking) {
        this.enableSmartBooking = enableSmartBooking;
    }

    public CreateLoyaltyPointsConfigDTO getLoyaltyPointsConfig() { return loyaltyPointsConfig; }

    public void setLoyaltyPointsConfig(CreateLoyaltyPointsConfigDTO loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
    }

    public CreateSessionSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(CreateSessionSettingsDTO settings) {
        this.settings = settings;
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
