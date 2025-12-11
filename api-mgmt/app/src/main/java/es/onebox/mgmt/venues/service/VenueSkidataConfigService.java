package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.accesscontrol.util.AccessControlValidationUtils;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.exception.ApiMgmtAccessControlErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.venues.converter.SkidataVenueConfigConverter;
import es.onebox.mgmt.venues.dto.SkidataVenueConfigDTO;
import es.onebox.mgmt.venues.utils.VenueValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueSkidataConfigService {

    @Autowired
    private VenueValidationUtils venueValidationUtils;
    @Autowired
    private AccessControlSystemsRepository accessControlSystemsRepository;
    @Autowired
    private SecurityManager securityManager;

    public SkidataVenueConfigDTO getVenueSkidataConfig(Long venueId, String systemName) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        return SkidataVenueConfigConverter.fromMsEvent(
                accessControlSystemsRepository.getVenueSkidataConfig(venueId, system));
    }

    public void createVenueSkidataConfig(Long venueId, String systemName, SkidataVenueConfigDTO config) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        validateSkidataConfig(config);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        accessControlSystemsRepository.createVenueSkidataConfig(venueId, system,
                SkidataVenueConfigConverter.toMsEvent(config));
    }

    public void modifyVenueSkidataConfig(Long venueId, String systemName, SkidataVenueConfigDTO config) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        validateSkidataConfig(config);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        accessControlSystemsRepository.modifyVenueSkidataConfig(venueId, system,
                SkidataVenueConfigConverter.toMsEvent(config));
    }

    public void deleteVenueSkidataConfig(Long venueId, String systemName) {
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        securityManager.checkEntityAccessible(venue.getEntity().getId());
        accessControlSystemsRepository.deleteVenueSkidataConfig(venueId, system);
    }

    private static void validateSkidataConfig(SkidataVenueConfigDTO config) {
        if (config == null) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_MANDATORY);
        }
        if (StringUtils.isEmpty(config.getHost())) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_HOST_MANDATORY);
        }
        if (config.getPort() == null || config.getPort() <= 0) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_PORT_MANDATORY);
        }
        if (config.getIssuer() == null || config.getIssuer() <= 0) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_ISSUER_MANDATORY);
        }
        if (config.getReceiver() == null || config.getReceiver() <= 0) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_RECEIVER_MANDATORY);
        }
    }
}
