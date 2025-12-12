package es.onebox.circuitcat.venues.converter;

import es.onebox.circuitcat.venues.dto.SectorDTO;
import es.onebox.circuitcat.venues.dto.VenueDTO;
import es.onebox.common.datasources.ms.venue.dto.Sector;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;

import java.util.ArrayList;

public class VenueConverter {

    public static VenueDTO convert(VenueTemplate venueTemplate) {
        VenueDTO venue = new VenueDTO();

        venue.setVenueConfigId(venueTemplate.getId());

        for (Sector sector : venueTemplate.getSectors()) {
            if (venue.getSectors() == null) {
                venue.setSectors(new ArrayList());
            }
            venue.getSectors().add(convert(sector));
        }

        return venue;
    }

    private static SectorDTO convert(Sector sector) {
        SectorDTO sectorDTO = new SectorDTO();
        sectorDTO.setCode(sector.getCode());
        sectorDTO.setName(sector.getName());
        return sectorDTO;
    }
}
