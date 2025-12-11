package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseSessionRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class FiltersSessionLinksSaleRequest extends BaseSessionRequestFilter {

    @Serial
    private static final long serialVersionUID = 1391627702758382677L;
    private List<Long> id;

    private SortOperator<String> sort;

    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private FilterWithOperator<ZonedDateTime> endDate;

    @JsonProperty("hour_range")
    private List<String> hourRange;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public FilterWithOperator<ZonedDateTime> getEndDate() {
        return endDate;
    }

    public void setEndDate(FilterWithOperator<ZonedDateTime> endDate) {
        this.endDate = endDate;
    }

    public List<String> getHourRange() {
        return hourRange;
    }

    public void setHourRange(List<String> hourRange) {
        this.hourRange = hourRange;
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
