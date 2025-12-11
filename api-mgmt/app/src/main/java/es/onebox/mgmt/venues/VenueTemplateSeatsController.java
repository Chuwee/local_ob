package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSeatsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateBaseSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSeatDTO;
import es.onebox.mgmt.venues.service.VenueTemplateSeatsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@RestController
@RequestMapping(VenueTemplateSeatsController.BASE_URI)
public class VenueTemplateSeatsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/seats";
    private static final String AUDIT_COLLECTION = "VENUE_TEMPLATES_SEAT";
    private static final String AUDIT_SUBCOLLECTION_TAGS = "VENUE_TEMPLATES_SEATS_TAG";
    private static final String AUDIT_SUBCOLLECTION_ASSIGN_TAGS = "VENUE_TEMPLATES_ASSIGN_TAG";

    private final VenueTemplateSeatsService venueTemplateSeatsService;

    @Autowired
    public VenueTemplateSeatsController(VenueTemplateSeatsService venueTemplateSeatsService) {
        this.venueTemplateSeatsService = venueTemplateSeatsService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET, value = "/{seatId}")
    public VenueTemplateSeatDTO getVenueTemplateSeat(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                     @PathVariable(value = "seatId") Integer seatId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateSeatsService.getVenueTemplateSeat(venueTemplateId, seatId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET)
    public List<VenueTemplateBaseSeatDTO> getVenueTemplateSeatsByRows(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                                      @RequestParam(value = "row_ids", required = false) @Valid @NotEmpty(message = "row_ids can not be empty") List<Integer> rowIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateSeatsService.getVenueTemplateSeatsByRows(venueTemplateId, rowIds);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public List<IdDTO> createVenueTemplateSeats(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                @RequestBody @Valid CreateVenueTemplateSeatsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return venueTemplateSeatsService.createVenueTemplateSeats(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeatTags(@PathVariable(value = "venueTemplateId") Long venueTemplateId, @RequestBody @Valid UpdateVenueTemplateSeatDTO[] seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_ASSIGN_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplateSeatsService.updateSeatTags(venueTemplateId, seats);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplateSeats(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                         @RequestParam(value = "seat_ids", required = false) @Valid @NotEmpty(message = "seat_ids can not be empty") List<Integer> seatIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplateSeatsService.deleteVenueTemplateSeats(venueTemplateId, seatIds);
    }
}
