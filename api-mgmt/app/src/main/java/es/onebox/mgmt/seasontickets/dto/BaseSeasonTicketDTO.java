package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.events.dto.EventVenueTemplateDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BaseSeasonTicketDTO implements Serializable, DateConvertible  {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("session_id")
    private Long sessionId;

    private String name;

    private String reference;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonIgnore
    private String startDateTZ;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    @JsonIgnore
    private String endDateTZ;

    private IdNameDTO entity;

    private IdNameDTO producer;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("venue_templates")
    private List<EventVenueTemplateDTO> venueTemplates;

    @JsonProperty("updating_capacity")
    private Boolean updatingCapacity;

    @JsonProperty("generating_capacity")
    private Boolean generatingCapacity;

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

    public Long getSessionId() {return sessionId;}

    public void setSessionId(Long sessionId) {this.sessionId = sessionId;}

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

    public String getCurrencyCode() { return currencyCode; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

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
