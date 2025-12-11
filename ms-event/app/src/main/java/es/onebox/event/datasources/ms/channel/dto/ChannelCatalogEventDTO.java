package es.onebox.event.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.datasources.ms.channel.enums.ChannelEventStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

public class ChannelCatalogEventDTO extends IdDTO {

    private static final long serialVersionUID = 1L;

    private Long eventId;
    private String eventName;
    private ChannelEventStatus status;
    private Boolean published;
    private Boolean onSale;
    private Boolean onCatalog;
    private Integer catalogPosition;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endDate;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ChannelEventStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelEventStatus status) {
        this.status = status;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Boolean getOnCatalog() {
        return onCatalog;
    }

    public void setOnCatalog(Boolean onCatalog) {
        this.onCatalog = onCatalog;
    }

    public Integer getCatalogPosition() {
        return catalogPosition;
    }

    public void setCatalogPosition(Integer catalogPosition) {
        this.catalogPosition = catalogPosition;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
