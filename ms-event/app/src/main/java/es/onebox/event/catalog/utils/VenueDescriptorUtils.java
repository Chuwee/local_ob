package es.onebox.event.catalog.utils;

import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;

public class VenueDescriptorUtils {

    private VenueDescriptorUtils() {
    }

    public static Integer getDefaultQuota(VenueDescriptor venueDescriptor) {
        return venueDescriptor.getQuotas()
                .stream()
                .filter(VenueQuota::getDefaultQuota)
                .findFirst()
                .map(VenueQuota::getId)
                .orElse(null);
    }
}
