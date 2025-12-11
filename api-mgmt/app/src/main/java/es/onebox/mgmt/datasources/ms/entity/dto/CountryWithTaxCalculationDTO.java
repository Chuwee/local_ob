package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CountryWithTaxCalculationDTO extends MasterdataValue {

    private String taxCalculation;

    public String getTaxCalculation() {
        return taxCalculation;
    }
    
    public void setTaxCalculation(String taxCalculation) {
        this.taxCalculation = taxCalculation;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[0]);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[0]);
    }
}
