package es.onebox.event.datasources.ms.ticket.dto.secondarymarket;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SecondaryMarketSearchFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private Long channelId;
    private Long sessionId;
    private List<Long> sessionIds;
    private List<Long> eventIds;
    private List<Long> seasonTicketIds;
    private List<Long> quotaIds;
    private List<Long> priceZoneIds;
    private List<Long> nnzIds;
    private List<SecondaryMarketStatus> status;


    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public List<Long> getSessionIds() { return sessionIds; }

    public void setSessionIds(List<Long> sessionIds) { this.sessionIds = sessionIds; }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getSeasonTicketIds() {
        return seasonTicketIds;
    }

    public void setSeasonTicketIds(List<Long> seasonTicketIds) {
        this.seasonTicketIds = seasonTicketIds;
    }

    public List<Long> getQuotaIds() { return quotaIds; }

    public void setQuotaIds(List<Long> quotaIds) { this.quotaIds = quotaIds; }

    public List<Long> getPriceZoneIds() {
        return priceZoneIds;
    }

    public void setPriceZoneIds(List<Long> priceZoneIds) {
        this.priceZoneIds = priceZoneIds;
    }

    public List<Long> getNnzIds() {
        return nnzIds;
    }

    public void setNnzIds(List<Long> nnzIds) {
        this.nnzIds = nnzIds;
    }

    public List<SecondaryMarketStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SecondaryMarketStatus> status) {
        this.status = status;
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
