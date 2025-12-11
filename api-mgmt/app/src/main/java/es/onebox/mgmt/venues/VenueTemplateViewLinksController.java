package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.CreateViewLinkDTO;
import es.onebox.mgmt.venues.service.VenueTemplateViewLinksService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(VenueTemplateViewLinksController.BASE_URI)
public class VenueTemplateViewLinksController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}";
    private static final String AUDIT_COLLECTION_LINKS = "VENUETEMPLATES_VIEW_LINKS";

    private final VenueTemplateViewLinksService venueTemplateViewLinksService;

    @Autowired
    public VenueTemplateViewLinksController(VenueTemplateViewLinksService venueTemplateViewLinksService) {
        this.venueTemplateViewLinksService = venueTemplateViewLinksService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST, value = "/views/{viewId}/links")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenueTemplateViewLink(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                             @PathVariable(value = "viewId") Integer viewId,
                                             @RequestBody @Valid CreateViewLinkDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_LINKS, AuditTag.AUDIT_ACTION_CREATE);
        return venueTemplateViewLinksService.createVenueTemplateViewLink(venueTemplateId, viewId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE, value = "/views/{viewId}/links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplateViewLink(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                            @PathVariable(value = "viewId") Long viewId,
                                            @PathVariable(value = "linkId") Long linkId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_LINKS, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateViewLinksService.deleteVenueTemplateViewLink(venueTemplateId, viewId, linkId);
    }
}
