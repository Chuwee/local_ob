package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.SkidataVenueConfigDTO;
import es.onebox.mgmt.venues.service.VenueSkidataConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@Validated
@RequestMapping(
        value = VenueSkidataConfigController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class VenueSkidataConfigController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venues/{venueId}/access-control-systems/{system:skidata.*}/config";

    private static final String AUDIT_COLLECTION = "VENUE_SKIDATA_CONFIG";

    @Autowired
    private VenueSkidataConfigService venueSkidataConfigService;

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public SkidataVenueConfigDTO getVenueSkidataConfig(@PathVariable(value = "venueId") Long venueId,
                                                       @PathVariable(value = "system") String system) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return venueSkidataConfigService.getVenueSkidataConfig(venueId, system);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Serializable> createVenueSkidataConfig(@PathVariable(value = "venueId") Long venueId,
                                                                 @PathVariable(value = "system") String system,
                                                                 @RequestBody SkidataVenueConfigDTO config) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        venueSkidataConfigService.createVenueSkidataConfig(venueId, system, config);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Serializable> modifyVenueSkidataConfig(@PathVariable(value = "venueId") Long venueId,
                                                                 @PathVariable(value = "system") String system,
                                                                 @RequestBody SkidataVenueConfigDTO config) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        venueSkidataConfigService.modifyVenueSkidataConfig(venueId, system, config);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Serializable> deleteVenueSkidataConfig(@PathVariable(value = "venueId") Long venueId,
                                                                 @PathVariable(value = "system") String system) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        venueSkidataConfigService.deleteVenueSkidataConfig(venueId, system);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
