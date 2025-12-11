package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductSessionSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -1401948736268915286L;

    private String freeSearch;
    private Boolean hasOverride;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private FilterWithOperator<ZonedDateTime> endDate;

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public Boolean getHasOverride() {
        return hasOverride;
    }

    public void setHasOverride(Boolean hasOverride) {
        this.hasOverride = hasOverride;
    }

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
