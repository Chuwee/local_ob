package es.onebox.event.seasontickets.dto;

import es.onebox.event.events.enums.Provider;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CreateSeasonTicketRequestDTO {

    @NotBlank(message = "name is mandatory")
    @Size(max = 50, message = "name cannot be above 50 characters")
    private String name;
    @NotNull(message = "entityId is mandatory")
    @Min(value = 1L, message = "entityId must be above 0")
    private Long entityId;
    @NotNull(message = "producerId is mandatory")
    @Min(value = 1L, message = "producerId must be above 0")
    private Long producerId;
    @NotNull(message = "categoryId is mandatory")
    @Min(value = 1L, message = "categoryId must be above 0")
    private Integer categoryId;

    private Integer defaultLangId;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer currencyId;
    private Integer invoicePrefixId;
    private Provider inventoryProvider;
    private Integer customCategoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDefaultLangId() {
        return defaultLangId;
    }

    public void setDefaultLangId(Integer defaultLangId) {
        this.defaultLangId = defaultLangId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonSurname() {
        return contactPersonSurname;
    }

    public void setContactPersonSurname(String contactPersonSurname) {
        this.contactPersonSurname = contactPersonSurname;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Integer getCurrencyId() { return currencyId; }

    public void setCurrencyId(Integer currencyId) { this.currencyId = currencyId; }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
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
