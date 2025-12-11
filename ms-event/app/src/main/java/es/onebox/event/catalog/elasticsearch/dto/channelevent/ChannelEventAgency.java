package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelEventAgency extends ChannelEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long eventId;
    @Id(index = 3)
    private Long agencyId;

    private ProfessionalClientConditions agencyConditions;

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
    public Long getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ProfessionalClientConditions getAgencyConditions() {
        return agencyConditions;
    }

    public void setAgencyConditions(ProfessionalClientConditions agencyConditions) {
        this.agencyConditions = agencyConditions;
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
