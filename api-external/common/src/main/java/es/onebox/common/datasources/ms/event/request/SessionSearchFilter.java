package es.onebox.common.datasources.ms.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.common.datasources.ms.event.enums.SessionType;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SessionSearchFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 3996359693255573174L;

    private List<Long> id;
    private List<SessionType> type;
    private SortOperator<String> sort;
    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> endDate;
    private List<SessionStatus> status;
    private List<Long> venueId;
    private List<Long> accessValidationSpaceIds;
    private List<Long> eventId;
    private Long entityId;
    private Long operatorId;
    private List<String> fields;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public List<SessionType> getType() {
        return type;
    }

    public void setType(List<SessionType> type) {
        this.type = type;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getEndDate() {
        return endDate;
    }

    public void setEndDate(List<FilterWithOperator<ZonedDateTime>> endDate) {
        this.endDate = endDate;
    }

    public List<SessionStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SessionStatus> status) {
        this.status = status;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
    }

    public List<Long> getAccessValidationSpaceIds() {
        return accessValidationSpaceIds;
    }

    public void setAccessValidationSpaceIds(List<Long> accessValidationSpaceIds) {
        this.accessValidationSpaceIds = accessValidationSpaceIds;
    }

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
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
