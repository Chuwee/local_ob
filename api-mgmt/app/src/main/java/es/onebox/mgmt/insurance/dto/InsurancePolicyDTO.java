package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.LanguagesDTO;

import java.io.Serial;

public class InsurancePolicyDTO extends InsurancePolicyBasicDTO {

    @Serial
    private static final long serialVersionUID = 7973844149975329809L;

    private String description;
    @JsonProperty("days_ahead_limit")
    private Integer daysAheadLimit;
    private Double taxes;
    @JsonProperty("insurer_benefits_fix")
    private Double insurerBenefitsFix;
    @JsonProperty("insurer_benefits_percent")
    private Double insurerBenefitsPercent;
    @JsonProperty("operator_benefits_fix")
    private Double operatorBenefitsFix;
    @JsonProperty("operator_benefits_percent")
    private Double operatorBenefitsPercent;
    @JsonProperty("default_allowed")
    private Boolean defaultAllowed;
    private LanguagesDTO languages;

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
