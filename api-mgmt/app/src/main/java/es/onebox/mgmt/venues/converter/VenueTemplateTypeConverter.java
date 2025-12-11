package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;

public class VenueTemplateTypeConverter {

    private VenueTemplateTypeConverter() {
    }

    public static VenueTemplateTypeDTO toVenueTemplateTypeDTO(VenueTemplateType venueTemplateType) {
        if (venueTemplateType == null) {
            return null;
        }
        switch (venueTemplateType) {
            case DEFAULT:
                return VenueTemplateTypeDTO.NORMAL;
            case AVET:
                return VenueTemplateTypeDTO.AVET;
            case ACTIVITY:
                return VenueTemplateTypeDTO.ACTIVITY;
            case THEME_PARK:
                return VenueTemplateTypeDTO.THEME_PARK;
            default:
                return null;
        }
    }

    public static VenueTemplateType toVenueTemplateType(VenueTemplateTypeDTO venueTemplateTypeDTO) {
        if (venueTemplateTypeDTO == null) {
            return null;
        }
        switch (venueTemplateTypeDTO) {
            case NORMAL:
                return VenueTemplateType.DEFAULT;
            case AVET:
                return VenueTemplateType.AVET;
            case ACTIVITY:
                return VenueTemplateType.ACTIVITY;
            case THEME_PARK:
                return VenueTemplateType.THEME_PARK;
            default:
                return null;
        }
    }
}
