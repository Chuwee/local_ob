package es.onebox.flc.venues.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.flc.datasources.msvenue.dto.VenueDTO;
import es.onebox.flc.datasources.msvenue.dto.VenuesDTO;
import es.onebox.flc.venues.dto.Space;
import es.onebox.flc.venues.dto.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VenueConverter {
    public static List<Venue> convert(VenuesDTO venues) {
        return venues.getData()
                .stream()
                .map(venueDTO -> convert(venueDTO))
                .collect(Collectors.toList());
    }

    public static Venue convert(VenueDTO venueDTO) {
        Venue venue = new Venue();

        venue.setVenueId(venueDTO.getId());
        venue.setCity(venueDTO.getCity());
        venue.setLogo(venueDTO.getPathLogo());
        venue.setName(venueDTO.getName());
        venue.setSpaces(getSpaces(venueDTO.getSpaces()));

        return venue;
    }

    private static List<Space> getSpaces(List<IdNameDTO> spaces) {
        List<Space> result = new ArrayList();

        for (IdNameDTO spaceDTO : spaces) {
            Space space = new Space();
            space.setSpaceId(spaceDTO.getId());
            space.setName(spaceDTO.getName());
            result.add(space);
        }

        return result;
    }
}
