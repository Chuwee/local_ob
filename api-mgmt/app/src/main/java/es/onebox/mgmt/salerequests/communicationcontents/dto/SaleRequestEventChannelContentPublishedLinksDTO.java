package es.onebox.mgmt.salerequests.communicationcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.BaseLinkDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SaleRequestEventChannelContentPublishedLinksDTO extends BaseLinkDTO {

    @Serial
    private static final long serialVersionUID = 2502648365118535630L;
    @JsonProperty("pending_generation")
    private Boolean pendingGeneration;

    @Override
    @JsonProperty("event_link")
    public String getLink() {
        return super.getLink();
    }

    public Boolean getPendingGeneration() {
        return pendingGeneration;
    }

    public void setPendingGeneration(Boolean pendingGeneration) {
        this.pendingGeneration = pendingGeneration;
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
