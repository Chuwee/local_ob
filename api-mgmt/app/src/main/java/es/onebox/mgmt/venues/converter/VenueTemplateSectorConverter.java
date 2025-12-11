package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.CloneVenueTemplateSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSector;
import es.onebox.mgmt.venues.dto.CloneVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSectorDTO;

public class VenueTemplateSectorConverter {

    private VenueTemplateSectorConverter() {
    }

    public static VenueTemplateSectorDTO fromMsVenue(Sector sector) {
        if (sector == null) {
            return null;
        }

        VenueTemplateSectorDTO venueTemplateSectorDTO = new VenueTemplateSectorDTO();
        venueTemplateSectorDTO.setId(sector.getId());
        venueTemplateSectorDTO.setName(sector.getName());
        venueTemplateSectorDTO.setCode(sector.getCode());
        venueTemplateSectorDTO.setColor(sector.getColor());
        venueTemplateSectorDTO.setDefault(sector.getDefault());
        venueTemplateSectorDTO.setOrder(sector.getOrder());

        return venueTemplateSectorDTO;
    }

    public static CloneVenueTemplateSector toMs(CloneVenueTemplateSectorRequestDTO requestDTO) {
        CloneVenueTemplateSector msDto = new CloneVenueTemplateSector();
        msDto.setName(requestDTO.getName());
        msDto.setCode(requestDTO.getCode());
        return msDto;
    }

    public static VenueTemplateSector toMs(CreateVenueTemplateSectorRequestDTO requestDTO) {
        VenueTemplateSector msDto = new VenueTemplateSector();
        msDto.setCode(requestDTO.getCode());
        msDto.setName(requestDTO.getName());
        msDto.setColor(requestDTO.getColor());
        return msDto;
    }

    public static VenueTemplateSector toMs(UpdateVenueTemplateSectorRequestDTO requestDTO) {
        VenueTemplateSector msDto = new VenueTemplateSector();
        msDto.setCode(requestDTO.getCode());
        msDto.setName(requestDTO.getName());
        msDto.setColor(requestDTO.getColor());
        msDto.setOrder(requestDTO.getOrder());
        return msDto;
    }
}
