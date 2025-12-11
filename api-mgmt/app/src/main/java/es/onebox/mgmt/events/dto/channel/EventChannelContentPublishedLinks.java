package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.BaseLinkDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EventChannelContentPublishedLinks extends BaseLinkDTO {

    private static final long serialVersionUID = -8878605506241048718L;

    @JsonProperty("sessions_links")
    private List<EventChannelContentSessionLink> sessionsLinks;

    @Override
    @JsonProperty("event_link")
    public String getLink() {
        return super.getLink();
    }

    public List<EventChannelContentSessionLink> getSessionsLinks() {
        return sessionsLinks;
    }

    public void setSessionsLinks(List<EventChannelContentSessionLink> sessionsLinks) {
        this.sessionsLinks = sessionsLinks;
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
