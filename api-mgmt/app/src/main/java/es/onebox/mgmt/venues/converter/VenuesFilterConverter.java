package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.VenuesFilter;
import es.onebox.mgmt.venues.dto.VenueSearchAggFilter;
import es.onebox.mgmt.venues.dto.VenueSearchFilter;

import java.util.List;

public class VenuesFilterConverter {

    private VenuesFilterConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenuesFilter convert(VenueSearchFilter venueSearchFilter, List<Long> visibleEntities) {
        if (venueSearchFilter == null) {
            return null;
        }
        VenuesFilter venuesFilter = new VenuesFilter();
        venuesFilter.setGrouped(false);

        venuesFilter.setCity(venueSearchFilter.getCity());
        venuesFilter.setCountryCode(venueSearchFilter.getCountryCode());
        venuesFilter.setEntityId(venueSearchFilter.getEntityId());
        venuesFilter.setEntityAdminId(venueSearchFilter.getEntityAdminId());
        venuesFilter.setFreeSearch(venueSearchFilter.getFreeSearch());
        venuesFilter.setIncludeOwnTemplateVenues(venueSearchFilter.getIncludeOwnTemplateVenues());
        venuesFilter.setIncludeThirdPartyVenues(venueSearchFilter.getIncludeThirdPartyVenues());
        venuesFilter.setOnlyInUseVenues(venueSearchFilter.getOnlyInUseVenues());
        venuesFilter.setVisibleEntities(visibleEntities);

        venuesFilter.setLimit(venueSearchFilter.getLimit());
        venuesFilter.setOffset(venueSearchFilter.getOffset());
        return venuesFilter;
    }

    public static VenuesFilter convertAgg(VenueSearchAggFilter venueSearchFilter) {
        if (venueSearchFilter == null) {
            return null;
        }
        VenuesFilter venuesFilter = new VenuesFilter();
        venuesFilter.setGrouped(true);

        venuesFilter.setEntityId(venueSearchFilter.getEntityId());
        venuesFilter.setEntityAdminId(venueSearchFilter.getEntityAdminId());
        venuesFilter.setIncludeOwnTemplateVenues(venueSearchFilter.getIncludeOwnTemplateVenues());
        venuesFilter.setIncludeThirdPartyVenues(venueSearchFilter.getIncludeThirdPartyVenues());
        venuesFilter.setOnlyInUseVenues(venueSearchFilter.getOnlyInUseVenues());

        venuesFilter.setLimit(venueSearchFilter.getLimit());
        venuesFilter.setOffset(venueSearchFilter.getOffset());
        return venuesFilter;
    }

}
