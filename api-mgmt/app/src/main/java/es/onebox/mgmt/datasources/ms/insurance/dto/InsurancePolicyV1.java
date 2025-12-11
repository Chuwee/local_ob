package es.onebox.mgmt.datasources.ms.insurance.dto;

import java.io.Serial;
import java.util.List;

public class InsurancePolicyV1 extends InsurancePolicyBasic {

    @Serial
    private static final long serialVersionUID = -718372978463726011L;

    private String description;
    private Integer daysAheadLimit;
    private Double taxes;
    private Double insurerBenefitsFix;
    private Double insurerBenefitsPercent;
    private Double operatorBenefitsFix;
    private Double operatorBenefitsPercent;
    private Boolean defaultAllowed;
    private List<InsurancePolicyLanguage> languages;


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

    public List<InsurancePolicyLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<InsurancePolicyLanguage> languages) {
        this.languages = languages;
    }

}
