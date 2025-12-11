package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestEventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class EventSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("event_type")
    private SaleRequestEventType eventType;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("entity")
    private IdNameDTO entity;
    @JsonProperty("venues")
    private List<VenueSaleRequestDTO> venues;

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

    public SaleRequestEventType getEventType() {
        return eventType;
    }

    public void setEventType(SaleRequestEventType eventType) {
        this.eventType = eventType;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public List<VenueSaleRequestDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueSaleRequestDTO> venues) {
        this.venues = venues;
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
