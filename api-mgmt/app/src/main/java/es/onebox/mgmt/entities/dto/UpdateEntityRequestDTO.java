package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.entities.enums.EntityStatus;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEntityRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3804614512717740964L;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @Length(max = 50, message = "name max size 50")
    private String name;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("nif")
    private String nif;

    @JsonProperty("social_reason")
    private String socialReason;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("status")
    private EntityStatus status;

    @JsonProperty("operator")
    private IdNameDTO operator;

    @JsonProperty("contact")
    private EntityContactDTO contact;

    @JsonProperty("invoice_data")
    @Valid
    private EntityInvoiceDataDTO invoiceData;

    @JsonProperty("settings")
    @Valid
    private EntitySettingsDTO settings;

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public IdNameDTO getOperator() {
        return operator;
    }

    public void setOperator(IdNameDTO operator) {
        this.operator = operator;
    }

    public EntityContactDTO getContact() {
        return contact;
    }

    public void setContact(EntityContactDTO contact) {
        this.contact = contact;
    }

    public EntityInvoiceDataDTO getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(EntityInvoiceDataDTO invoiceData) {
        this.invoiceData = invoiceData;
    }

    public EntitySettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EntitySettingsDTO settings) {
        this.settings = settings;
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
