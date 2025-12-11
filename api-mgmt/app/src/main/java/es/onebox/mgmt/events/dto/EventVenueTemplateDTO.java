package es.onebox.mgmt.events.dto;

import java.io.Serializable;

public class EventVenueTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private EventVenueDTO venue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(EventVenueDTO venue) {
        this.venue = venue;
    }
}
