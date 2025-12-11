package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseSessionRequestFilter;
import es.onebox.mgmt.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class SessionSearchFilter extends BaseSessionRequestFilter {

    @Serial
    private static final long serialVersionUID = 3996359693255573174L;

    private List<Long> id;

    private List<SessionType> type;

    @JsonProperty("venue_template_id")
    private Long venueTemplateId;

    private SortOperator<String> sort;

    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private FilterWithOperator<ZonedDateTime> endDate;

    @JsonProperty("hour_range")
    private List<String> hourRange;

    @JsonProperty("get_queueit_info")
    private boolean getQueueitInfo;

    @JsonProperty("include_dynamic_price_config")
    private Boolean includeDynamicPriceConfig;

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

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
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

    public boolean isGetQueueitInfo() {
        return getQueueitInfo;
    }

    public void setGetQueueitInfo(boolean getQueueitInfo) {
        this.getQueueitInfo = getQueueitInfo;
    }

    public Boolean getIncludeDynamicPriceConfig() {
        return includeDynamicPriceConfig;
    }

    public void setIncludeDynamicPriceConfig(Boolean includeDynamicPriceConfig) {
        this.includeDynamicPriceConfig = includeDynamicPriceConfig;
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
