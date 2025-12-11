package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EventChannelContentAllLinks extends EventChannelContentPublishedLinks {

    private static final long serialVersionUID = -6825849696059549214L;

    @JsonProperty("unpublished_sessions_links")
    private List<EventChannelContentSessionLink> unpublishedSessionsLinks;

    public List<EventChannelContentSessionLink> getUnpublishedSessionsLinks() {
        return unpublishedSessionsLinks;
    }

    public void setUnpublishedSessionsLinks(List<EventChannelContentSessionLink> unpublishedSessionsLinks) {
        this.unpublishedSessionsLinks = unpublishedSessionsLinks;
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
