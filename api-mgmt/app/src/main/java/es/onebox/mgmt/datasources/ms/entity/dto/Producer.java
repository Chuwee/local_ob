package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.ProducerStatus;

public class Producer extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private String nif;
    private String socialReason;
    private ProducerStatus status;
    private Boolean isDefault;
    private Entity entity;

    private String address;
    private String postalCode;
    private String city;
    private Integer countryId;
    private Integer countrySubdivisionId;
    private String contactName;
    private String email;
    private String phone;
    private Boolean useSimplifiedInvoice;

    public ProducerStatus getStatus() {
        return status;
    }

    public void setStatus(ProducerStatus status) {
        this.status = status;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCountrySubdivisionId() {
        return countrySubdivisionId;
    }

    public void setCountrySubdivisionId(Integer countrySubdivisionId) {
        this.countrySubdivisionId = countrySubdivisionId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getUseSimplifiedInvoice() {
        return useSimplifiedInvoice;
    }

    public void setUseSimplifiedInvoice(Boolean useSimplifiedInvoice) {
        this.useSimplifiedInvoice = useSimplifiedInvoice;
    }
}
