package es.onebox.mgmt.events.tours.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TourDTO extends BaseTourDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TourEventDTO> events;

    public List<TourEventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<TourEventDTO> events) {
        this.events = events;
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
