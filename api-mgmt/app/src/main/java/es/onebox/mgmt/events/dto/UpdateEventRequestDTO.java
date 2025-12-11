package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2656881452883120440L;

    private String name;

    private Boolean archived;

    @Size(message = "event reference length cannot be above 100 characters", max = 100)
    private String reference;

    private EventStatus status;

    private EventContactDTO contact;

    @JsonProperty("currency_code")
    private String currencyCode;

    @Valid
    private UpdateEventSettingsDTO settings;

    @JsonProperty("phone_verification_required")
    private Boolean phoneVerificationRequired;

    @JsonProperty("attendant_verification_required")
    private Boolean attendantVerificationRequired;

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

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public EventContactDTO getContact() {
        return contact;
    }

    public void setContact(EventContactDTO contact) {
        this.contact = contact;
    }

    public String getCurrencyCode() { return currencyCode; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public UpdateEventSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(UpdateEventSettingsDTO settings) {
        this.settings = settings;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean getPhoneVerificationRequired() {
        return phoneVerificationRequired;
    }

    public void setPhoneVerificationRequired(Boolean phoneVerificationRequired) {
        this.phoneVerificationRequired = phoneVerificationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
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
