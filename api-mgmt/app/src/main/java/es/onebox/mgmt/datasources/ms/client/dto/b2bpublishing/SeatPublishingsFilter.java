package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;


public class SeatPublishingsFilter extends BaseRequestFilter {

    private Long operatorId;
    private Long entityAdminId;
    @NotEmpty(message = "entity ID is a mandatory filter")
    private List<Long> entityIds;
    private List<Long> channelIds;
    private List<Long> eventIds;
    private List<Long> sessionIds;
    private List<Long> clientIds;
    private List<Long> clientEntityIds;
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private List<TransactionType> types;
    private List<TicketStatus> status;
    private String q;
    private SortOperator<String> sort;

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

    public List<Long> getClientEntityIds() {
        return clientEntityIds;
    }
    public void setClientEntityIds(List<Long> clientEntityIds) {
        this.clientEntityIds = clientEntityIds;
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

    public List<TransactionType> getTypes() {
        return types;
    }
    public void setTypes(List<TransactionType> types) {
        this.types = types;
    }

    public List<TicketStatus> getStatus() {
        return status;
    }
    public void setStatus(List<TicketStatus> status) {
        this.status = status;
    }

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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }
}
