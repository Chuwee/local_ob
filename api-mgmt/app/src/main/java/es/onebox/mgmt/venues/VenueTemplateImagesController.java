package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.UpsertVenueTemplateImageDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateImageDTO;
import es.onebox.mgmt.venues.service.VenueTemplateImagesService;
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

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(VenueTemplateImagesController.BASE_URI)
public class VenueTemplateImagesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/images";
    private static final String AUDIT_COLLECTION_IMAGES = "VENUETEMPLATES_IMAGES";

    private final VenueTemplateImagesService venueTemplateImagesService;

    @Autowired
    public VenueTemplateImagesController(VenueTemplateImagesService venueTemplateImagesService) {
        this.venueTemplateImagesService = venueTemplateImagesService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET)
    public List<VenueTemplateImageDTO> getVenueTemplateImages(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_IMAGES, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateImagesService.getVenueTemplateImages(venueTemplateId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public VenueTemplateImageDTO upsertVenueTemplateImage(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                          @RequestBody @Valid UpsertVenueTemplateImageDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_IMAGES, AuditTag.AUDIT_ACTION_CREATE);
        return venueTemplateImagesService.upsertVenueTemplateImage(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplateViewLink(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                            @PathVariable(value = "imageId") Long imageId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_IMAGES, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplateImagesService.deleteVenueTemplateImage(venueTemplateId, imageId);
    }
}
