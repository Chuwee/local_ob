package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateVipViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsFilterDTO;
import es.onebox.mgmt.venues.service.VenueTemplateViewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(VenueTemplateViewsController.BASE_URI)
public class VenueTemplateViewsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/views";
    private static final String AUDIT_COLLECTION = "VENUETEMPLATES_VIEWS";

    private final VenueTemplateViewsService venueTemplateViewsService;

    @Autowired
    public VenueTemplateViewsController(VenueTemplateViewsService venueTemplateViewsService) {
        this.venueTemplateViewsService = venueTemplateViewsService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenueTemplateView(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                         @RequestBody @Valid CreateVenueTemplateViewDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return venueTemplateViewsService.createVenueTemplateView(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET)
    public VenueTemplateViewsDTO getVenueTemplateViews(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                       @BindUsingJackson VenueTemplateViewsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateViewsService.getVenueTemplateViews(venueTemplateId, filter);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/vip")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateVipViews(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                            @RequestParam(required = false, value = "session_id") Long sessionId,
                                            @RequestBody @Valid UpdateVenueTemplateVipViewsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateViewsService.updateVenueTemplateViewsVip(venueTemplateId, sessionId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateViews(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                         @RequestBody @Valid UpdateVenueTemplateViewsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateViewsService.updateVenueTemplateViews(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/{viewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateView(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                        @PathVariable(value = "viewId") Long viewId,
                                        @RequestBody @Valid UpdateVenueTemplateViewDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateViewsService.updateVenueTemplateView(venueTemplateId, viewId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET, value = "/root")
    public VenueTemplateViewDTO getVenueTemplateRootView(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateViewsService.getVenueTemplateRootView(venueTemplateId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET, value = "/{viewId}")
    public VenueTemplateViewDTO getVenueTemplateView(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                     @PathVariable(value = "viewId") Long viewId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateViewsService.getVenueTemplateView(venueTemplateId, viewId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{viewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplateView(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                        @PathVariable(value = "viewId") Long viewId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateViewsService.deleteVenueTemplateView(venueTemplateId, viewId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/{viewId}/template", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateViewTemplate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                @PathVariable(value = "viewId") Long viewId,
                                                @RequestBody @Size(max = 2000000,
                                                        message = "The template size is greater than maximum allowed of 2 million characters")
                                                String template) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateViewsService.updateVenueTemplateViewTemplate(venueTemplateId, viewId, template);
    }
}
