package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateScope;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;

public class VenueTemplateScopeConverter {

    private VenueTemplateScopeConverter() {
    }

    public static VenueTemplateScopeDTO toVenueTemplateScopeDTO(VenueTemplateScope scope) {
        if (scope == null) {
            return null;
        }
        switch (scope) {
            case VENUE:
                return VenueTemplateScopeDTO.ARCHETYPE;
            case CAPACITIES:
                return VenueTemplateScopeDTO.STANDARD;
            default:
                return null;
        }
    }

    public static VenueTemplateScope toVenueTemplateScope(VenueTemplateScopeDTO dto) {
        if (dto == null) {
            return null;
        }
        switch (dto) {
            case ARCHETYPE:
                return VenueTemplateScope.VENUE;
            case STANDARD:
                return VenueTemplateScope.CAPACITIES;
            default:
                return null;
        }
    }
}
