package es.onebox.event.seasontickets.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.elasticsearch.SearchSessionsSortableField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(20)
public class SeasonTicketSessionsSearchFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 1164820198216677906L;

    private Long eventId;
    private Long sessionId;
    private String freeSearch;
    private SessionAssignationStatusDTO assignationStatus;
    private SortOperator<SearchSessionsSortableField> sort;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public SessionAssignationStatusDTO getAssignationStatus() {
        return assignationStatus;
    }

    public void setAssignationStatus(SessionAssignationStatusDTO assignationStatus) {
        this.assignationStatus = assignationStatus;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public SortOperator<SearchSessionsSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<SearchSessionsSortableField> sort) {
        this.sort = sort;
    }

    @Override
    protected Long getDefaultLimit() {
        return 50L;
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
