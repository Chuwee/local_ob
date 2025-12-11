package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.events.enums.EventStatus;
import es.onebox.mgmt.events.enums.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;


public class BaseEventDTO implements Serializable, DateConvertible {

    @Serial
    private static final long serialVersionUID = -293963538352559567L;

    private Long id;

    private String name;

    private String reference;

    private EventType type;

    private EventStatus status;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonIgnore
    private String startDateTZ;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    @JsonIgnore
    private String endDateTZ;

    private IdNameDTO entity;

    @JsonProperty("currency_code")
    private String currencyCode;

    private IdNameDTO producer;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("venue_templates")
    private List<EventVenueTemplateDTO> venueTemplates;

    private EventContactDTO contact;

    private Boolean archived;

    @JsonProperty("phone_verification_required")
    private Boolean phoneVerificationRequired;

    @JsonProperty("attendant_verification_required")
    private Boolean attendantVerificationRequired;

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

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStartDateTZ() {
        return startDateTZ;
    }

    public void setStartDateTZ(String startDateTZ) {
        this.startDateTZ = startDateTZ;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEndDateTZ() {
        return endDateTZ;
    }

    public void setEndDateTZ(String endDateTZ) {
        this.endDateTZ = endDateTZ;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public String getCurrencyCode() { return currencyCode; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }

    public List<EventVenueTemplateDTO> getVenueTemplates() {
        return venueTemplates;
    }

    public void setVenueTemplates(List<EventVenueTemplateDTO> venueTemplates) {
        this.venueTemplates = venueTemplates;
    }

    public EventContactDTO getContact() {
        return contact;
    }

    public void setContact(EventContactDTO contact) {
        this.contact = contact;
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
    public void convertDates() {
        if (startDate != null && startDateTZ != null) {
            this.startDate = this.startDate.withZoneSameInstant(ZoneId.of(startDateTZ));
        }
        if (endDate != null && endDateTZ != null) {
            this.endDate = this.endDate.withZoneSameInstant(ZoneId.of(endDateTZ));
        }
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
