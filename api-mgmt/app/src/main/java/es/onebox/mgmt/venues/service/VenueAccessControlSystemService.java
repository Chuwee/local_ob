package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.accesscontrol.converter.AccessControlSystemsConverter;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.accesscontrol.util.AccessControlValidationUtils;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.exception.ApiMgmtAccessControlErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.venues.utils.VenueValidationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueAccessControlSystemService {

    @Autowired
    private AccessControlSystemsRepository accessControlSystemsRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private VenueValidationUtils venueValidationUtils;


    public NameDTO getVenueAccessControlSystemAssociation(Long venueId){
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        List<AccessControlSystem> systems = accessControlSystemsRepository.findByVenueId(venueId);
        if(CollectionUtils.isEmpty(systems)){
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.VENUE_HAS_NO_SYSTEM);
        }
        return AccessControlSystemsConverter.convertFrom(systems).get(0);
    }

    public void createVenueAccessControlSystemAssociation(Long venueId, NameDTO systemName) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        accessControlSystemsRepository.createAccessControlSystemVenue(venueId, system);
    }

    public void deleteVenueAccessControlSystemAssociation(Long venueId, String systemName) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        accessControlSystemsRepository.deleteAccessControlSystemVenue(venueId, system);
    }

}
