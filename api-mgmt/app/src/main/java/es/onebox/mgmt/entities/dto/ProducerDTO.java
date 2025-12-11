package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.entities.enums.ProducerStatus;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ProducerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @Size(max = 200, message = "name must not be greater than 200 characters")
    private String name;
    private ProducerStatus status;
    @JsonProperty("default")
    private Boolean isDefault;
    @Size(max = 30, message = "nif must not be greater than 30 characters")
    private String nif;
    @Size(max = 100, message = "social reason must not be greater than 100 characters")
    @JsonProperty("social_reason")
    private String socialReason;
    private IdNameDTO entity;
    @JsonProperty("use_simplified_invoice")
    private Boolean useSimplifiedInvoice;


    private ProducerContactDTO contact;

    public ProducerDTO() {
    }

    public ProducerDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public ProducerContactDTO getContact() {
        return contact;
    }

    public void setContact(ProducerContactDTO contact) {
        this.contact = contact;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Boolean getUseSimplifiedInvoice() {
        return useSimplifiedInvoice;
    }

    public void setUseSimplifiedInvoice(Boolean useSimplifiedInvoice) {
        this.useSimplifiedInvoice = useSimplifiedInvoice;
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
