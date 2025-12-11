package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.b2b.publishing.enums.TransactionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeatPublishingsSearchRequest extends BaseRequestFilter implements Serializable {

    private Long operatorId;
    @JsonProperty("entity_ids")
    private List<Long> entityIds;
    @JsonProperty("channel_ids")
    private List<Long> channelIds;
    @JsonProperty("client_entity_ids")
    private List<Long> clientEntityIds;
    @JsonProperty("client_ids")
    private List<Long> clientIds;
    @JsonProperty("event_ids")
    private List<Long> eventIds;
    @JsonProperty("session_ids")
    private List<Long> sessionIds;
    private List<TransactionType> types;
    @JsonProperty("date_from")
    private ZonedDateTime dateFrom;
    @JsonProperty("date_to")
    private ZonedDateTime dateTo;
    //private List<TicketStatus> status; TODO BreakPoint allow this filter when webhook or conciliation is done
    private String q;
    @JsonProperty("sort")
    private SortOperator<String> sort;

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public List<Long> getEntityIds() {
        return entityIds;
    }
    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }
    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getClientEntityIds() {
        return clientEntityIds;
    }
    public void setClientEntityIds(List<Long> clientEntityIds) {
        this.clientEntityIds = clientEntityIds;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }
    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }
    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Long> getClientIds() {
        return clientIds;
    }
    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<TransactionType> getTypes() {
        return types;
    }
    public void setTypes(List<TransactionType> types) {
        this.types = types;
    }

    public ZonedDateTime getDateFrom() {
        return dateFrom;
    }
    public void setDateFrom(ZonedDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public ZonedDateTime getDateTo() {
        return dateTo;
    }
    public void setDateTo(ZonedDateTime dateTo) {
        this.dateTo = dateTo;
    }

    /*public List<TicketStatus> getStatus() {
        return status;
    }
    public void setStatus(List<TicketStatus> status) {
        this.status = status;
    }*/

    public String getQ() {
        return q;
    }
    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<String> getSort() { return sort; }
    public void setSort(SortOperator<String> sort) { this.sort = sort; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
