package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.dal.dto.common.enums.EventStatus;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;


@DefaultLimit(50)
@MaxLimit(50)
public class SearchSaleRequestsFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 2L;

    @JsonProperty("channel_entity_id")
    private List<Long> channelEntityId;

    @JsonProperty("event_entity_id")
    private List<Long> eventEntityId;

    @JsonProperty("channel_id")
    private List<Long> channelId;

    @JsonProperty("include_archived")
    private Boolean includeArchived;

    @JsonProperty("include_third_party_entity_events")
    private Boolean includeThirdPartyEntityEvents;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> date;

    private List<SaleRequestsStatus> status;

    @JsonProperty("event_status")
    private List<EventStatus> eventStatus;

    private List<String> fields;

    private SortOperator<String> sort;

    private String q;

    private String currency;

    public List<Long> getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(List<Long> channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public List<Long> getEventEntityId() {
        return eventEntityId;
    }

    public void setEventEntityId(List<Long> eventEntityId) {
        this.eventEntityId = eventEntityId;
    }

    public List<Long> getChannelId() {
        return channelId;
    }

    public void setChannelId(List<Long> channelId) {
        this.channelId = channelId;
    }

    public Boolean getIncludeArchived() {
        return includeArchived;
    }

    public void setIncludeArchived(Boolean includeArchived) {
        this.includeArchived = includeArchived;
    }

    public Boolean getIncludeThirdPartyEntityEvents() {
        return includeThirdPartyEntityEvents;
    }

    public void setIncludeThirdPartyEntityEvents(Boolean includeThirdPartyEntityEvents) {
        this.includeThirdPartyEntityEvents = includeThirdPartyEntityEvents;
    }

    public List<FilterWithOperator<ZonedDateTime>> getDate() {
        return date;
    }

    public void setDate(List<FilterWithOperator<ZonedDateTime>> date) {
        this.date = date;
    }

    public List<SaleRequestsStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SaleRequestsStatus> status) {
        this.status = status;
    }

    public List<EventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
