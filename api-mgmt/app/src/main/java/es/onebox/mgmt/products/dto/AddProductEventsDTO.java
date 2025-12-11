package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AddProductEventsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("event_ids")
    private List<Long> eventIds;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventId) {
        this.eventIds = eventId;
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
