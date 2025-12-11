package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.repository.SpacesRepository;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.venues.converter.SpaceConverter;
import es.onebox.mgmt.venues.dto.VenueSpaceDTO;
import es.onebox.mgmt.venues.dto.VenueSpacePostRequest;
import es.onebox.mgmt.venues.dto.VenueSpacePutRequest;
import es.onebox.mgmt.venues.dto.VenueSpacesResponse;
import es.onebox.mgmt.venues.utils.VenueValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpacesService {

    private final SpacesRepository spacesRepository;
    private final VenueValidationUtils venueValidationUtils;
    private final SecurityManager securityManager;

    @Autowired
    public SpacesService(SpacesRepository spacesRepository,
                         VenueValidationUtils venueValidationUtils,
                         SecurityManager securityManager){
        this.spacesRepository = spacesRepository;
        this.venueValidationUtils = venueValidationUtils;
        this.securityManager = securityManager;
    }

    public VenueSpacesResponse getVenueSpaces(Long venueId) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessibleWithVisibility(foundVenue.getEntity().getId());

        return SpaceConverter.toVenueSpacesResponse(spacesRepository.getVenueSpaces(venueId));
    }

    public VenueSpaceDTO getVenueSpace(Long venueId, Long spaceId) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);
        if(!CommonUtils.isTrue(foundVenue.getPublic())
            || !securityManager.isSameOperator(foundVenue.getEntity().getId())) {
            securityManager.checkEntityAccessibleWithVisibility(foundVenue.getEntity().getId());
        }

        checkSpaceBelongsToVenue(foundVenue, spaceId);

        return SpaceConverter.toSpaceDTO(
                spacesRepository.getVenueSpace(venueId, spaceId)
        );
    }

    public IdDTO createVenueSpace(Long venueId, VenueSpacePostRequest newSpace) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(foundVenue.getEntity().getId());

        return spacesRepository.createVenueSpace(SpaceConverter.toSpaceDTO(venueId, null, newSpace));
    }

    public void updateVenueSpace(Long venueId, Long spaceId, VenueSpacePutRequest patchedSpace) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(foundVenue.getEntity().getId());
        checkSpaceBelongsToVenue(foundVenue, spaceId);

        spacesRepository.updateVenueSpace(SpaceConverter.toSpaceDTO(venueId, spaceId, patchedSpace));
    }

    public void deleteVenueSpace(Long venueId, Long spaceId) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(foundVenue.getEntity().getId());
        checkSpaceBelongsToVenue(foundVenue, spaceId);

        spacesRepository.deleteVenueSpace(venueId, spaceId);
    }

    private static void checkSpaceBelongsToVenue(Venue requestedVenue, Long spaceId) {
        Optional<Long> spaceID = requestedVenue.getSpaces().stream().map(IdDTO::getId).filter(id -> id.equals(spaceId)).findAny();
        if (spaceID.isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtVenueErrorCode.VENUE_CONTAINS_NO_SUCH_SPACE).build();
        }
    }
}
