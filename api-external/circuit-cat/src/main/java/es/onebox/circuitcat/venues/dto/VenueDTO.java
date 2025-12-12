package es.onebox.circuitcat.venues.dto;

import es.onebox.common.datasources.ms.venue.dto.Sector;

import java.io.Serializable;
import java.util.List;

public class VenueDTO implements Serializable {

    private static final long serialVersionUID = 2042745781638723725L;
    Long venueConfigId;
    List<SectorDTO> sectors;

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
    }
}
