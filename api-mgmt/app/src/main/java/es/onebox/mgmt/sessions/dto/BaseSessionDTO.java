package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.sessions.enums.SessionGenerationStatus;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BaseSessionDTO implements Serializable, DateConvertible {


    @Serial
    private static final long serialVersionUID = 2208023384291508576L;

    private Long id;

    private String name;

    private SessionType type;

    private SessionStatus status;

    @JsonProperty("generation_status")
    private SessionGenerationStatus generationStatus;

    private SessionReleaseFlagStatus release;

    private SessionSaleFlagStatus sale;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    private IdNameDTO event;

    private IdNameDTO entity;

    @JsonProperty("venue_template")
    private VenueTemplateDTO venueTemplate;

    @JsonProperty("session_ids")
    private List<Long> sessionIds;

    private String reference;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("publication_cancelled_reason")
    private String publicationCancelledReason;

    @JsonProperty("release_enabled")
    private Boolean releaseEnabled;

    @JsonProperty("updating_capacity")
    private Boolean updatingCapacity;

    @JsonProperty("generating_capacity")
    private Boolean generatingCapacity;

    @JsonProperty("archived")
    private Boolean archived;

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

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public VenueTemplateDTO getVenueTemplate() {
        return venueTemplate;
    }

    public void setVenueTemplate(VenueTemplateDTO venueTemplate) {
        this.venueTemplate = venueTemplate;
    }

    public SessionReleaseFlagStatus getRelease() {
        return release;
    }

    public void setRelease(SessionReleaseFlagStatus release) {
        this.release = release;
    }

    public SessionSaleFlagStatus getSale() {
        return sale;
    }

    public void setSale(SessionSaleFlagStatus sale) {
        this.sale = sale;
    }

    public SessionGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SessionGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getPublicationCancelledReason() {
        return publicationCancelledReason;
    }

    public void setPublicationCancelledReason(String publicationCancelledReason) {
        this.publicationCancelledReason = publicationCancelledReason;
    }

    public Boolean getReleaseEnabled() {
        return releaseEnabled;
    }

    public void setReleaseEnabled(Boolean releaseEnabled) {
        this.releaseEnabled = releaseEnabled;
    }

    public Boolean getUpdatingCapacity() {
        return updatingCapacity;
    }

    public void setUpdatingCapacity(Boolean updatingCapacity) {
        this.updatingCapacity = updatingCapacity;
    }

    public Boolean getGeneratingCapacity() {
        return generatingCapacity;
    }

    public void setGeneratingCapacity(Boolean generatingCapacity) {
        this.generatingCapacity = generatingCapacity;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    @Override
    public void convertDates() {
        if (venueTemplate != null && venueTemplate.getVenue() != null && venueTemplate.getVenue().getTimezone() != null) {
            if (startDate != null) {
                this.startDate = this.startDate.withZoneSameInstant(ZoneId.of(venueTemplate.getVenue().getTimezone()));
            }
            if (this.endDate != null) {
                this.endDate = this.endDate.withZoneSameInstant(ZoneId.of(venueTemplate.getVenue().getTimezone()));
            }
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
