package es.onebox.mgmt.countries.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;

import java.io.Serial;
import java.io.Serializable;

public class CountryDTO extends IdNameCodeDTO implements Serializable {
    
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("tax_calculation")
    private String taxCalculation;

    public String getTaxCalculation() {
        return taxCalculation;
    }
    
    public void setTaxCalculation(String taxCalculation) {
        this.taxCalculation = taxCalculation;
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