package es.onebox.mgmt.sessions;


import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoImagesDeleteFilterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.sessions.dto.templateelementsinfo.SessionVenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoStatusRequestDTO;
import es.onebox.mgmt.venues.enums.ElementType;
import es.onebox.mgmt.venues.service.VenueTemplateElementInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(SessionTemplateElementsInfoController.BASE_URI)
public class SessionTemplateElementsInfoController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/sessions/{sessionId}/venue-template-elements-info";
    private static final String AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO = "VENUE_TEMPLATES_ELEMENTS_INFO";


    private final VenueTemplateElementInfoService venueTemplateElementInfoService;

    public SessionTemplateElementsInfoController(VenueTemplateElementInfoService venueTemplateElementInfoService) {
        this.venueTemplateElementInfoService = venueTemplateElementInfoService;
    }

    @GetMapping
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public SessionVenueTemplateElementInfoSearchResponseDTO searchSessionTemplateElementsInfo(@PathVariable Long sessionId,
                                                                                              @Valid VenueTemplateSessionElementInfoSearchDTO requestSearch) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateElementInfoService.searchSessionVenueTemplateElementsInfo(sessionId, requestSearch);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public void createTemplateInfoByIdAndSessionId(@PathVariable("sessionId") Long sessionId,
                                                   @RequestBody VenueTemplateSessionElementDefaultInfoCreateDTO request){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_CREATE);
        venueTemplateElementInfoService.createSessionTemplateElementInfo(sessionId, request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public void bulkUpdateSessionTemplateInfo(@PathVariable("sessionId") Long sessionId,
                                              @Valid VenueTemplateElementInfoSearchDTO requestSearch,
                                              @RequestBody VenueTemplateSessionElementInfoBulkUpdateRequestDTO request){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateElementInfoService.bulkUpdateSessionVenueTemplateElementInfo(sessionId, request, requestSearch);
    }

    @GetMapping("/{elementType}/{elementId}")
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public VenueTemplateElementInfoSessionResponseDTO getTemplateInfoBySession(@PathVariable("sessionId") Long sessionId,
                                                                               @PathVariable ElementType elementType,
                                                                               @PathVariable Long elementId){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateElementInfoService.getSessionVenueTemplateElementInfo(sessionId, elementType, elementId);
    }

    @PutMapping("/{elementType}/{elementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public void updateTemplateInfoByIdAndSessionId(@PathVariable("sessionId") Long sessionId,
                                                   @PathVariable ElementType elementType, @PathVariable Long elementId,
                                                   @RequestBody VenueTemplateElementInfoSessionUpdateDTO request){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateElementInfoService.updateSessionVenueTemplateElementInfo(sessionId, elementType, elementId, request);
    }

    @DeleteMapping("/{elementType}/{elementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public void deleteTemplateInfoByIdAndSessionId(@PathVariable("sessionId") Long sessionId,
                                                   @PathVariable ElementType elementType, @PathVariable Long elementId){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateElementInfoService.deleteSessionVenueTemplateElementInfo(sessionId, elementType, elementId);
    }

    @PutMapping("/{elementType}/{elementId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public void updateStatusSessionVenueTemplateElementInfo(@PathVariable("sessionId") Long sessionId,
                                                            @PathVariable ElementType elementType, @PathVariable Long elementId,
                                                            @RequestBody VenueTemplateSessionElementInfoStatusRequestDTO requestDTO ){
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateElementInfoService.updateStatusSessionVenueTemplateElementInfo(sessionId, elementType, elementId, requestDTO);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{elementType}/{elementId}/images/{imageType}/languages/{language}")
    public void deleteSessionTemplateElementInfoImages(@PathVariable Long sessionId,
                                                       @PathVariable ElementType elementType,
                                                       @PathVariable Long elementId,
                                                       @PathVariable ElementInfoImageType imageType,
                                                       @PathVariable String language,
                                                       TemplateInfoImagesDeleteFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateElementInfoService
                .deleteSessionTemplateElementInfoImages(sessionId, elementType, elementId, imageType, language, filter);
    }

}
