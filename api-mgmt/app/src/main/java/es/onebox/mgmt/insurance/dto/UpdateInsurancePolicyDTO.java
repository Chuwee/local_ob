package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class UpdateInsurancePolicyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1490997740735058249L;

    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;
    private Boolean active;
    @Size(max = 500, message = "description length cannot be above 500 characters")
    private String description;
    @JsonProperty("days_ahead_limit")
    private Integer daysAheadLimit;
    @Min(value = 0, message = "taxes must be equal or above 0")
    @Max(value = 100, message = "taxes must be equal or below 100")
    private Double taxes;
    @JsonProperty("insurer_benefits_fix")
    private Double insurerBenefitsFix;
    @JsonProperty("insurer_benefits_percent")
    @Min(value = 0, message = "insurer_benefits_percent must be equal or above 0")
    @Max(value = 100, message = "insurer_benefits_percent must be equal or below 100")
    private Double insurerBenefitsPercent;
    @JsonProperty("operator_benefits_fix")
    private Double operatorBenefitsFix;
    @JsonProperty("operator_benefits_percent")
    @Min(value = 0, message = "operator_benefits_percent must be equal or above 0")
    @Max(value = 100, message = "operator_benefits_percent must be equal or below 100")
    private Double operatorBenefitsPercent;
    @JsonProperty("external_provider")
    @Size(max = 50, message = "external_provider length cannot be above 50 characters")
    private String externalProvider;
    @JsonProperty("default_allowed")
    private Boolean defaultAllowed;
    private LanguagesDTO languages;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDaysAheadLimit() {
        return daysAheadLimit;
    }

    public void setDaysAheadLimit(Integer daysAheadLimit) {
        this.daysAheadLimit = daysAheadLimit;
    }

    public Double getTaxes() {
        return taxes;
    }

    public void setTaxes(Double taxes) {
        this.taxes = taxes;
    }

    public Double getInsurerBenefitsFix() {
        return insurerBenefitsFix;
    }

    public void setInsurerBenefitsFix(Double insurerBenefitsFix) {
        this.insurerBenefitsFix = insurerBenefitsFix;
    }

    public Double getInsurerBenefitsPercent() {
        return insurerBenefitsPercent;
    }

    public void setInsurerBenefitsPercent(Double insurerBenefitsPercent) {
        this.insurerBenefitsPercent = insurerBenefitsPercent;
    }

    public Double getOperatorBenefitsFix() {
        return operatorBenefitsFix;
    }

    public void setOperatorBenefitsFix(Double operatorBenefitsFix) {
        this.operatorBenefitsFix = operatorBenefitsFix;
    }

    public Double getOperatorBenefitsPercent() {
        return operatorBenefitsPercent;
    }

    public void setOperatorBenefitsPercent(Double operatorBenefitsPercent) {
        this.operatorBenefitsPercent = operatorBenefitsPercent;
    }

    public String getExternalProvider() {
        return externalProvider;
    }

    public void setExternalProvider(String externalProvider) {
        this.externalProvider = externalProvider;
    }

    public Boolean getDefaultAllowed() {
        return defaultAllowed;
    }

    public void setDefaultAllowed(Boolean defaultAllowed) {
        this.defaultAllowed = defaultAllowed;
    }

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }
}
