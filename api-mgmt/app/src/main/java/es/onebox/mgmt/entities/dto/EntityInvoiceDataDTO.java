package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.validation.annotation.IBAN;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EntityInvoiceDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;

    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("country")
    private CodeNameDTO country;

    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;

    @JsonProperty("allow_external_notification")
    private Boolean allowExternalInvoiceNotification;

    @JsonProperty("bank_account")
    @IBAN
    private String bankAccount;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public CodeNameDTO getCountry() {
        return country;
    }

    public void setCountry(CodeNameDTO country) {
        this.country = country;
    }

    public CodeNameDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CodeNameDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Boolean getAllowExternalInvoiceNotification() {
        return allowExternalInvoiceNotification;
    }

    public void setAllowExternalInvoiceNotification(Boolean allowExternalInvoiceNotification) {
        this.allowExternalInvoiceNotification = allowExternalInvoiceNotification;
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
