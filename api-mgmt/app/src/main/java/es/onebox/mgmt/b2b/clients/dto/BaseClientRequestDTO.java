package es.onebox.mgmt.b2b.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.b2b.clients.enums.ClientCategoryType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

public class BaseClientRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("category_type")
    private ClientCategoryType categoryType;

    @Length(max = 50, message = "name max size 50")
    private String name;

    @Length(max = 50, message = "tax_id max size 50")
    @JsonProperty("tax_id")
    private String taxId;

    @Length(max = 100, message = "business_name max size 100")
    @JsonProperty("business_name")
    private String businessName;

    private CodeNameDTO country;

    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;

    @JsonProperty("contact_data")
    private ContactDataDTO contactData;

    @Length(max = 300, message = "description max size 300")
    private String description;
    private List<String> keywords;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
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
