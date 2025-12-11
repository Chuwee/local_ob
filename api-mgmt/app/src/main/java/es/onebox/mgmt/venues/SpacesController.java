package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.VenueSpaceDTO;
import es.onebox.mgmt.venues.dto.VenueSpacePostRequest;
import es.onebox.mgmt.venues.dto.VenueSpacePutRequest;
import es.onebox.mgmt.venues.dto.VenueSpacesResponse;
import es.onebox.mgmt.venues.service.SpacesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@Validated
@RequestMapping(
        value = SpacesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SpacesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venues/{venueId}/spaces";

    private static final String AUDIT_COLLECTION = "VENUE_SPACES";

    private final SpacesService spacesService;

    @Autowired
    public SpacesController(SpacesService spacesService){
        this.spacesService = spacesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @ResponseStatus(HttpStatus.OK)
    public VenueSpacesResponse getVenueSpaces(@PathVariable Long venueId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return spacesService.getVenueSpaces(venueId);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "{spaceId}")
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @ResponseStatus(HttpStatus.OK)
    public VenueSpaceDTO getVenueSpace(@PathVariable Long venueId,
                                       @PathVariable Long spaceId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return spacesService.getVenueSpace(venueId, spaceId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenueSpace(@PathVariable Long venueId,
                                  @BindUsingJackson @Valid @RequestBody VenueSpacePostRequest newSpace){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return spacesService.createVenueSpace(venueId, newSpace);
    }

    @RequestMapping(method = RequestMethod.PUT,
                    value = "{spaceId}")
    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueSpace(@PathVariable Long venueId,
                                 @PathVariable Long spaceId,
                                 @BindUsingJackson @Valid @RequestBody VenueSpacePutRequest patchedSpace){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        spacesService.updateVenueSpace(venueId, spaceId, patchedSpace);
    }

    @RequestMapping(method = RequestMethod.DELETE,
                    value = "{spaceId}")
    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueSpace(@PathVariable Long venueId,
                                 @PathVariable Long spaceId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        spacesService.deleteVenueSpace(venueId, spaceId);
    }
}
