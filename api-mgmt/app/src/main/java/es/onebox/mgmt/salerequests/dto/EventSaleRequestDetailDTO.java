package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EventSaleRequestDetailDTO extends EventSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("contact_person")
    private ContactPersonDTO contactPerson;
    @JsonProperty("category")
    private CategoriesSaleRequestExtendedDTO category;
    @JsonProperty("currency_code")
    private String currency;
    @JsonProperty("tax_mode")
    private TaxModeDTO taxMode;

    public ContactPersonDTO getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPersonDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    public CategoriesSaleRequestExtendedDTO getCategory() {
        return category;
    }

    public void setCategory(CategoriesSaleRequestExtendedDTO category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
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
