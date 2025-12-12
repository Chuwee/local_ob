package es.onebox.circuitcat.venues.dto;

import java.io.Serializable;
import java.util.List;

public class VenueConfigDTO  implements Serializable {

    private static final long serialVersionUID = -6411840962195394592L;

    List<VenueDTO> venues;

    public List<VenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueDTO> venues) {
        this.venues = venues;
    }
}
