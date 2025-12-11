package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateStatus;
import es.onebox.mgmt.venues.enums.VenueTemplateStatusDTO;

public class VenueTemplateStatusConverter {

    private VenueTemplateStatusConverter() {
    }

    public static VenueTemplateStatusDTO toVenueTemplateStatusDTO(VenueTemplateStatus venueTemplateStatus) {
        if (venueTemplateStatus == null) {
            return null;
        }
        switch (venueTemplateStatus) {
            case ACTIVE:
                return VenueTemplateStatusDTO.ACTIVE;
            case IN_PROGRESS:
                return VenueTemplateStatusDTO.IN_PROGRESS;
            case ERROR:
                return VenueTemplateStatusDTO.ERROR;
            default:
                return null;
        }
    }

    public static VenueTemplateStatus toVenueTemplateStatus(VenueTemplateStatusDTO venueTemplateStatusDTO) {
        switch (venueTemplateStatusDTO) {
            case ACTIVE:
                return VenueTemplateStatus.ACTIVE;
            case IN_PROGRESS:
                return VenueTemplateStatus.IN_PROGRESS;
            case ERROR:
                return VenueTemplateStatus.ERROR;
            default:
                return null;
        }
    }
}
