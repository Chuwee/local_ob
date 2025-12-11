package es.onebox.mgmt.venues.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueStatus;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VenueValidationUtils {

    @Autowired
    private VenuesRepository venuesRepository;

    public Venue validateVenueId(Long venueId) {
        if (venueId == null || venueId <= 0) {
            throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_ID_MANDATORY);
        }
        Venue venue = venuesRepository.getVenue(venueId);
        if (venue == null || venue.getStatus().equals(VenueStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_NOT_FOUND, "No venue with id: " + venueId, null);
        }
        return venue;
    }

    public Long validateArchetypeTemplate(Long venueId, Long templateEntityId, Boolean graphic, Long fromTemplateId) {
        if (venueId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "venue_id is mandatory on ARCHETYPE scope", null);
        }
        if (fromTemplateId != null) {
            Venue venue = venuesRepository.getVenue(venueId);
            templateEntityId = venue.getEntity().getId();
        }
        if (fromTemplateId == null && graphic == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "graphic is mandatory on ARCHETYPE type templates", null);
        }
        return templateEntityId;
    }

    public static Long validateStandardTemplate(Long entityId, VenueTemplateTypeDTO type, Boolean graphic, Long fromTemplateId) {
        Long templateEntityId;
        if (entityId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "entity_id is mandatory on STANDARD scope", null);
        }
        if (type == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "type is mandatory on STANDARD scope", null);
        } else if (fromTemplateId == null && VenueTemplateTypeDTO.NORMAL.equals(type) && graphic == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "graphic is mandatory on NORMAL type templates", null);
        }
        templateEntityId = entityId;
        return templateEntityId;
    }

}
