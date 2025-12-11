package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.service.VenueAccessControlSystemService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@Validated
@RequestMapping(
        value = VenueAccessControlSystemController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class VenueAccessControlSystemController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venues/{venueId}/access-control-systems";

    private static final String AUDIT_COLLECTION = "VENUE_ACCESS_CONTROL_SYSTEM";

    @Autowired
    private VenueAccessControlSystemService venueAccessControlSystemServiceService;

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public NameDTO getVenueAccessControlSystemAssociation(@PathVariable(value = "venueId") Long venueId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return venueAccessControlSystemServiceService.getVenueAccessControlSystemAssociation(venueId);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Serializable> createVenueAccessControlSystemAssociation(
            @PathVariable(value = "venueId") Long venueId,
            @RequestBody NameDTO system) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        venueAccessControlSystemServiceService.createVenueAccessControlSystemAssociation(venueId, system);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{accessControlSystem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueAccessControlSystemAssociation(
            @PathVariable(value = "venueId") Long venueId,
            @PathVariable(value = "accessControlSystem") String system) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        venueAccessControlSystemServiceService.deleteVenueAccessControlSystemAssociation(venueId, system);
    }
}
