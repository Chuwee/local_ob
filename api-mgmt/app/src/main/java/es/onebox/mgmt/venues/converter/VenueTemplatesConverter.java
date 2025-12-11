package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplates;
import es.onebox.mgmt.venues.dto.SearchVenueTemplatesResponse;

import java.util.stream.Collectors;

public class VenueTemplatesConverter {

    private VenueTemplatesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static SearchVenueTemplatesResponse fromMsVenue(VenueTemplates venueTemplates) {
        if (venueTemplates == null) {
            return null;
        }
        SearchVenueTemplatesResponse response = new SearchVenueTemplatesResponse();
        response.setData(venueTemplates.getData().stream()
                .map(VenueTemplateConverter::convert)
                .collect(Collectors.toList()));
        response.setMetadata(venueTemplates.getMetadata());

        return response;
    }
}
