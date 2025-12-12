package es.onebox.eci.locations.converter;

import es.onebox.common.datasources.catalog.dto.common.Venue;
import es.onebox.eci.locations.dto.Address;
import es.onebox.eci.locations.dto.Location;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocationConverter {
    public static List<Location> convert(List<Venue> venues) {
        if (venues == null) {
            return Collections.emptyList();
        }

        return venues
                .stream()
                .map(LocationConverter::convert)
                .collect(Collectors.toList());
    }

    public static Location convert(Venue venue) {
        Location location = new Location();
        location.setIdentifier(String.valueOf(venue.getId()));
        location.setName(venue.getName());
        location.setAddress(getAdress(venue));
        return location;
    }

    private static Address getAdress(Venue venue) {
        Address address = new Address();
        address.setPostalCode(venue.getLocation().getPostalCode());
        address.setAddressCountry(venue.getLocation().getCountry().getCode());
        address.setAddressLocality(venue.getLocation().getCity());
        address.setAddressRegion(venue.getLocation().getCountrySubdivision().getName());
        return address;
    }
}
