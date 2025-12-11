package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

public class EventChannelContentSessionLink extends IdNameDTO {

    private static final long serialVersionUID = -1451447944159577231L;

    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;
    private String link;
    private Boolean enabled;
    @JsonProperty("pending_generation")
    private Boolean pendingGeneration;
    @JsonProperty("virtual_queue")
    private EventChannelContentSessionLinkQueueIt queueit;
    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPendingGeneration() {
        return pendingGeneration;
    }

    public void setPendingGeneration(Boolean pendingGeneration) {
        this.pendingGeneration = pendingGeneration;
    }

    public EventChannelContentSessionLinkQueueIt getQueueit() {
        return queueit;
    }

    public void setQueueit(EventChannelContentSessionLinkQueueIt queueit) {
        this.queueit = queueit;
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
