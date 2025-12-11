package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViewOrientation;

public class VenueTemplateViewOrientationConverter {

    private VenueTemplateViewOrientationConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenueTemplateViewOrientation toMs(es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation orientation) {
        if (orientation == null) {
            return null;
        }
        return switch (orientation) {
            case EAST -> VenueTemplateViewOrientation.EAST;
            case WEST -> VenueTemplateViewOrientation.WEST;
            case NORTH -> VenueTemplateViewOrientation.NORTH;
            case SOUTH -> VenueTemplateViewOrientation.SOUTH;
        };
    }

    public static es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation fromMs(VenueTemplateViewOrientation orientation) {
        if (orientation == null) {
            return null;
        }
        return switch (orientation) {
            case EAST -> es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation.EAST;
            case WEST -> es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation.WEST;
            case NORTH -> es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation.NORTH;
            case SOUTH -> es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation.SOUTH;
        };
    }
}
