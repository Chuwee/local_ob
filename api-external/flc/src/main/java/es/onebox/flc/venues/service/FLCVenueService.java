package es.onebox.flc.venues.service;

import es.onebox.flc.datasources.msvenue.dto.VenueDTO;
import es.onebox.flc.datasources.msvenue.dto.VenuesDTO;
import es.onebox.flc.datasources.msvenue.repository.MsVenueRepository;
import es.onebox.flc.utils.AuthenticationUtils;
import es.onebox.flc.venues.converter.VenueConverter;
import es.onebox.flc.venues.dto.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FLCVenueService {

    @Autowired
    private MsVenueRepository msVenueRepository;

    public List<Venue> getVenues(Boolean inUse, Long limit, Long offset) {

        Integer entityId = (Integer) AuthenticationUtils.getAttribute("entityId");
        Integer operatorId = (Integer) AuthenticationUtils.getAttribute("operatorId");

        VenuesDTO venues = msVenueRepository.getVenues(entityId, operatorId, inUse, limit, offset);

        for (VenueDTO venue : venues.getData()) {
            VenueDTO venueDetail = msVenueRepository.getVenue(venue.getId());
            venue.setSpaces(venueDetail.getSpaces());
            venue.setPathLogo(venueDetail.getPathLogo());
        }

        return VenueConverter.convert(venues);
    }
}
