package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.MsSaleRequestsStatus;
import es.onebox.common.datasources.ms.channel.enums.EventStatus;
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

public class MsSaleRequestsFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 8388639040242319855L;
    private Long operatorId;
    private List<Long> channelEntityId;
    private List<Long> eventEntityId;
    private List<Long> channelId;
    private List<Long> eventId;
    private Long entityAdminId;
    private Boolean includeArchived;
    private Boolean includeAllowedChannelPromotion;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> date;
    private List<MsSaleRequestsStatus> status;
    private List<String> fields;
    private SortOperator<String> sort;
    private String q;
    private List<EventStatus> eventStatus;

    public MsSaleRequestsFilter() {
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

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

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Boolean getIncludeArchived() {
        return includeArchived;
    }

    public void setIncludeArchived(Boolean includeArchived) {
        this.includeArchived = includeArchived;
    }

    public Boolean getIncludeAllowedChannelPromotion() {
        return includeAllowedChannelPromotion;
    }

    public void setIncludeAllowedChannelPromotion(Boolean includeAllowedChannelPromotion) {
        this.includeAllowedChannelPromotion = includeAllowedChannelPromotion;
    }

    public List<FilterWithOperator<ZonedDateTime>> getDate() {
        return date;
    }

    public void setDate(List<FilterWithOperator<ZonedDateTime>> date) {
        this.date = date;
    }

    public List<MsSaleRequestsStatus> getStatus() {
        return status;
    }

    public void setStatus(List<MsSaleRequestsStatus> status) {
        this.status = status;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }

    public List<EventStatus> getEventStatus() {
        return eventStatus;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
