package es.onebox.mgmt.b2b.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.b2b.clients.enums.ClientCategoryType;
import es.onebox.mgmt.b2b.clients.enums.ClientStatus;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BaseClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("entity")
    private IdNameDTO entity;

    @JsonProperty("category_type")
    @NotNull(message = "category_type can not be null")
    private ClientCategoryType categoryType;

    @JsonProperty("name")
    @NotNull(message = "name can not be null")
    private String name;

    @JsonProperty("tax_id")
    @NotNull(message = "tax_id can not be null")
    private String taxId;

    @JsonProperty("iata_code")
    private String iataCode;

    @JsonProperty("business_name")
    @NotNull(message = "business_name can not be null")
    private String businessName;

    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;

    @JsonProperty("country")
    @NotNull(message = "country can not be null")
    private CodeNameDTO country;

    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;

    @JsonProperty("contact_data")
    @NotNull(message = "contact_data can not be null")
    private ContactDataDTO contactData;

    @JsonProperty("status")
    private ClientStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public ClientCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(ClientCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
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

    public ContactDataDTO getContactData() {
        return contactData;
    }

    public void setContactData(ContactDataDTO contactData) {
        this.contactData = contactData;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
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
