package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductEventsFilterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> eventIds;
    private SelectionType sessionSelectionType;
    private ProductEventStatus status;
    private List<EventStatus> eventStatus;
    private FilterWithOperator<ZonedDateTime> startDate;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public SelectionType getSessionSelectionType() {
        return sessionSelectionType;
    }

    public void setSessionSelectionType(SelectionType sessionSelectionType) {
        this.sessionSelectionType = sessionSelectionType;
    }

    public ProductEventStatus getStatus() {
        return status;
    }

    public void setStatus(ProductEventStatus status) {
        this.status = status;
    }

    public List<EventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }

    public FilterWithOperator<ZonedDateTime> getStartDate() {
        return startDate;
    }

    public void setStartDate(FilterWithOperator<ZonedDateTime> startDate) {
        this.startDate = startDate;
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
