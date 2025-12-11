package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRowDTO;
import es.onebox.mgmt.venues.service.VenueTemplateRowsService;
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
@RequestMapping(VenueTemplateRowsController.BASE_URI)
public class VenueTemplateRowsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/rows";
    private static final String AUDIT_COLLECTION = "VENUE_TEMPLATES_ROW";
    private static final String AUDIT_SUBCOLLECTION_TAGS = "VENUE_TEMPLATES_ROWS_TAG";

    private final VenueTemplateRowsService venueTemplateRowsService;

    @Autowired
    public VenueTemplateRowsController(VenueTemplateRowsService venueTemplateRowsService) {
        this.venueTemplateRowsService = venueTemplateRowsService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET, value = "/{rowId}")
    public VenueTemplateRowDTO getVenueTemplateRow(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                   @PathVariable(value = "rowId") Integer rowId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateRowsService.getVenueTemplateRow(venueTemplateId, rowId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenueTemplateRow(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                        @RequestBody @Valid CreateVenueTemplateRowDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return new IdDTO(venueTemplateRowsService.createVenueTemplateRow(venueTemplateId, body));
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<IdDTO> createVenueTemplateRows(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                               @RequestBody @Valid CreateVenueTemplateRowsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return venueTemplateRowsService.createVenueTemplateRows(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/{rowId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateRow(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                       @PathVariable(value = "rowId") Long rowId,
                                       @RequestBody UpdateVenueTemplateRowDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplateRowsService.updateVenueTemplateRow(venueTemplateId, rowId, requestDTO);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateRows(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                        @RequestBody @Valid UpdateVenueTemplateRowsDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplateRowsService.updateVenueTemplateRows(venueTemplateId, requestDTO);
    }


    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{rowId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplateRow(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                       @PathVariable(value = "rowId") Long rowId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplateRowsService.deleteVenueTemplateRow(venueTemplateId, rowId);
    }
}
