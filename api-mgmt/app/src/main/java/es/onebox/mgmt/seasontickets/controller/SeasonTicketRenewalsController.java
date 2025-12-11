package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.CountRenewalsPurgeResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalCandidatesSeasonTicketsResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalEntitiesResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalPurgeFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketsRenewalsExportRequest;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeasonTicketRenewalAvailableSeatsDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeasonTicketRenewalCapacityTreeDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketRenewalsService;
import jakarta.validation.Valid;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_TAQ;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@Validated
@RequestMapping(
        value = SeasonTicketRenewalsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketRenewalsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String RENEWALS = "/renewals";
    private static final String SEASON_TICKET_RENEWALS = "/{seasonTicketId}/renewals";
    private static final String RENEWAL_ID = "/{renewalId}";
    private static final String SEASON_TICKET_RENEWAL_ID = SEASON_TICKET_RENEWALS + RENEWAL_ID;

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS";
    private static final String RENEWALS_ACTION = "RENEWALS";

    private final SeasonTicketRenewalsService seasonTicketRenewalsService;
    private final ExportService exportService;

    @Autowired
    public SeasonTicketRenewalsController(SeasonTicketRenewalsService seasonTicketRenewalsService, ExportService exportService) {
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
        this.exportService = exportService;
    }

    private void checkParams(Long seasonTicketId, String message) {
        if (seasonTicketId == null || seasonTicketId < 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, message, null);
        }
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            value = "/{seasonTicketId}/renewal-candidates",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RenewalCandidatesSeasonTicketsResponse searchRenewalCandidatesSeasonTickets(@PathVariable Long seasonTicketId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, "RENEWAL_CANDIDATES");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.searchRenewalCandidatesSeasonTickets(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            value = "/{seasonTicketId}/renewal-entities",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RenewalEntitiesResponse searchRenewalEntities(@PathVariable Long seasonTicketId,
                                                         @BindUsingJackson @Valid SeasonTicketRenewalFilter filter) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, "RENEWAL_ENTITIES");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.searchRenewalEntities(seasonTicketId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(
            method = RequestMethod.POST,
            value = SEASON_TICKET_RENEWALS,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> setRenewalSeasonTicketDTO(@PathVariable Long seasonTicketId, @RequestBody @Valid RenewalSeasonTicketDTO renewalSeasonTicketDTO) {
        Audit.addTags(AuditTag.AUDIT_ACTION_ADD, AUDIT_COLLECTION, RENEWALS_ACTION);
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        if (renewalSeasonTicketDTO == null || renewalSeasonTicketDTO.getRenewalSeasonTicket() == null
                && !Boolean.TRUE.equals(renewalSeasonTicketDTO.getExternalEvent())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "renewal_season_ticket is required", null);
        } else if (renewalSeasonTicketDTO.getRenewalExternalEvent() == null
                && Boolean.TRUE.equals(renewalSeasonTicketDTO.getExternalEvent())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "renewal_external_event is required", null);
        }
        seasonTicketRenewalsService.setRenewalSeasonTicketDTO(seasonTicketId, renewalSeasonTicketDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(
            method = RequestMethod.GET,
            value = SEASON_TICKET_RENEWALS)
    public SeasonTicketRenewalsResponse getRenewalsSeasonTicket(@PathVariable Long seasonTicketId, @BindUsingJackson @Valid SeasonTicketRenewalFilter filter) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, RENEWALS_ACTION);
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.getRenewalsSeasonTicket(seasonTicketId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_TAQ})
    @RequestMapping(
            method = RequestMethod.GET,
            value = RENEWALS)
    public SeasonTicketRenewalsResponse getRenewals(@BindUsingJackson @Valid SeasonTicketRenewalsFilter filter) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, RENEWALS_ACTION);
        return seasonTicketRenewalsService.getRenewals(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(
            method = RequestMethod.GET,
            value = SEASON_TICKET_RENEWALS + "/capacity-tree")
    public SeasonTicketRenewalCapacityTreeDTO getCapacityTree(@PathVariable Long seasonTicketId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, "CAPACITY_TREE");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.getCapacityTree(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(
            method = RequestMethod.GET,
            value = SEASON_TICKET_RENEWALS + "/row/{rowId}/available-seats")
    public SeasonTicketRenewalAvailableSeatsDTO getAvailableSeats(@PathVariable Long seasonTicketId,
                                                                  @PathVariable Long rowId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, "AVAILABLE_SEATS");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        checkParams(rowId, "Invalid rowId");
        return seasonTicketRenewalsService.getAvailableSeats(seasonTicketId, rowId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(
            method = RequestMethod.GET,
            value = SEASON_TICKET_RENEWALS + "/not-numbered-zones/{notNumberedZoneId}/available-seats")
    public SeasonTicketRenewalAvailableSeatsDTO getAvailableSeatsByNotNumberedZone(@PathVariable Long seasonTicketId,
                                                                                   @PathVariable Long notNumberedZoneId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, "AVAILABLE_SEATS");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        checkParams(notNumberedZoneId, "Invalid notNumberedZoneId");
        return seasonTicketRenewalsService.getAvailableSeatsByNotNumberedZone(seasonTicketId, notNumberedZoneId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = PUT, value = SEASON_TICKET_RENEWALS)
    public UpdateRenewalResponseDTO updateRenewalSeats(@PathVariable Long seasonTicketId, @RequestBody @Valid UpdateRenewalRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_ACTION_UPDATE, AUDIT_COLLECTION, RENEWALS_ACTION);
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.updateRenewalSeats(seasonTicketId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = DELETE, value = SEASON_TICKET_RENEWAL_ID)
    public ResponseEntity<Serializable> deleteRenewalSeat(@PathVariable Long seasonTicketId, @PathVariable String renewalId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_DELETE, AUDIT_COLLECTION, RENEWALS_ACTION);
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        if (renewalId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "renewalId id is mandatory", null);
        }
        seasonTicketRenewalsService.deleteRenewalSeat(seasonTicketId, renewalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = SEASON_TICKET_RENEWALS)
    public DeleteRenewalsResponseDTO deleteRenewalSeats(@PathVariable Long seasonTicketId, @BindUsingJackson @Valid DeleteRenewalsRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_ACTION_DELETE, AUDIT_COLLECTION, RENEWALS_ACTION);
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.deleteRenewalSeats(seasonTicketId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = SEASON_TICKET_RENEWALS + "/purge")
    public ResponseEntity<Serializable> purgeSeasonTicketRenewalSeats(@PathVariable Long seasonTicketId,
                                                                      @BindUsingJackson @Valid SeasonTicketRenewalPurgeFilter filter) {
        Audit.addTags(AuditTag.AUDIT_ACTION_DELETE, AUDIT_COLLECTION, "PURGE_ACTION");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        seasonTicketRenewalsService.purgeSeasonTicketRenewalSeats(seasonTicketId, filter);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = SEASON_TICKET_RENEWALS + "/purge")
    public CountRenewalsPurgeResponseDTO countRenewalsPurge(@PathVariable Long seasonTicketId,
                                                            @BindUsingJackson @Valid SeasonTicketRenewalPurgeFilter filter) {
        Audit.addTags(AuditTag.AUDIT_ACTION_DELETE, AUDIT_COLLECTION, "PURGE_COUNT_ACTION");
        checkParams(seasonTicketId, "Invalid seasonTicketId");
        return seasonTicketRenewalsService.countRenewalsPurge(seasonTicketId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = SEASON_TICKET_RENEWALS + "/exports")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long seasonTicketId,
                                 @BindUsingJackson @Valid SeasonTicketRenewalFilter filter,
                                 @Valid @RequestBody SeasonTicketsRenewalsExportRequest requestBody) {
        Audit.addTags(AuditTag.AUDIT_ACTION_EXPORT, AUDIT_COLLECTION, RENEWALS_ACTION);
        return this.exportService.exportSeasonTicketsRenewals(seasonTicketId, filter, requestBody);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = RENEWALS + "/exports/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable String exportId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, RENEWALS_ACTION);
        return this.exportService.getSeasonTicketsRenewalsExportStatus(exportId);
    }
}
