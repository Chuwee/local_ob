package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.Map;
import java.util.Set;

public class ChannelSessionAgency extends ChannelSession {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long sessionId;
    @Id(index = 3)
    private Long agencyId;
    private Map<Long, Set<String>> priceTypeTags;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public Long getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    @Override
    public Long getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Map<Long, Set<String>> getPriceTypeTags() {
        return priceTypeTags;
    }

    public void setPriceTypeTags(Map<Long, Set<String>> priceTypeTags) {
        this.priceTypeTags = priceTypeTags;
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
