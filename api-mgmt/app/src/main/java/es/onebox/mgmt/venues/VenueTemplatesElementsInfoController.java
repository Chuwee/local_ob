package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoImagesDeleteFilterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoDefaultResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.venues.enums.ElementType;
import es.onebox.mgmt.venues.service.VenueTemplateElementInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@Validated
@RequestMapping(VenueTemplatesElementsInfoController.BASE_URI)
public class VenueTemplatesElementsInfoController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/elements-info";
    private static final String AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO = "VENUE_TEMPLATES_ELEMENTS_INFO";
    private final VenueTemplateElementInfoService venueTemplateElementInfoService;

    @Autowired
    public VenueTemplatesElementsInfoController(VenueTemplateElementInfoService venueTemplateElementInfoService) {
        this.venueTemplateElementInfoService = venueTemplateElementInfoService;
    }
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping()
    public VenueTemplateElementInfoSearchResponseDTO searchVenueTemplateElementsInfo(@PathVariable Long venueTemplateId,
                                                                     @Valid VenueTemplateElementInfoSearchDTO requestSearch) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateElementInfoService.searchVenueTemplateElementsInfo(venueTemplateId, requestSearch);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    public void createVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                               @Valid @RequestBody VenueTemplateElementDefaultInfoCreateDTO elementDefaultInfoDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_ADD);
        venueTemplateElementInfoService.createTemplateElementInfo(venueTemplateId, elementDefaultInfoDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping()
    public void bulkUpdateVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                                   @Valid VenueTemplateElementInfoSearchDTO requestSearch,
                                               @Valid @RequestBody VenueTemplateElementInfoBulkUpdateRequestDTO bulkUpdateRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateElementInfoService.bulkUpdateTemplateElementInfo(venueTemplateId, bulkUpdateRequestDTO, requestSearch);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping()
    public void bulkDeleteVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                                   @BindUsingJackson VenueTemplateElementInfoBulkRequestDTO filters) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateElementInfoService.bulkDeleteVenueTemplateElementInfo(venueTemplateId, filters);
    }

    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping("/{elementType}/{elementId}")
    public VenueTemplateElementInfoDefaultResponseDTO getVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                                                                  @PathVariable ElementType elementType,
                                                                                  @PathVariable Long elementId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateElementInfoService.getVenueTemplateElementsInfo(venueTemplateId, elementType, elementId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{elementType}/{elementId}")
    public void updateVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                               @PathVariable ElementType elementType, @PathVariable Long elementId,
                                               @Valid @Validated @RequestBody VenueTemplateElementDefaultInfoUpdateDTO elementDefaultInfoUpdateDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplateElementInfoService.updateTemplateElementInfo(venueTemplateId, elementType, elementId, elementDefaultInfoUpdateDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{elementType}/{elementId}")
    public void deleteVenueTemplateElementInfo(@PathVariable Long venueTemplateId,
                                               @PathVariable ElementType elementType, @PathVariable Long elementId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateElementInfoService.deleteTemplateElementInfo(venueTemplateId, elementType, elementId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{elementType}/{elementId}/images/{imageType}/languages/{language}")
    public void deleteTemplateElementInfoImages(@PathVariable Long venueTemplateId,
                                                @PathVariable ElementType elementType,
                                                @PathVariable Long elementId,
                                                @PathVariable ElementInfoImageType imageType,
                                                @PathVariable String language,
                                                TemplateInfoImagesDeleteFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_VENUE_TEMPLATE_ELEMENTS_INFO, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateElementInfoService
                .deleteTemplateElementInfoImages(venueTemplateId, elementType, elementId, imageType, language, filter);
    }

}
