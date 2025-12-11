package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.sessions.dto.SessionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductSessionDeliveryPointsFilterDTO extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -1401948736268915282L;

    private List<Long> ids;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private FilterWithOperator<ZonedDateTime> endDate;
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();
    private String olsonId;
    private List<SessionStatus> status = new ArrayList<>();
    private String freeSearch;

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public FilterWithOperator<ZonedDateTime> getEndDate() {
        return endDate;
    }

    public void setEndDate(FilterWithOperator<ZonedDateTime> endDate) {
        this.endDate = endDate;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<SessionStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SessionStatus> status) {
        this.status = status;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

