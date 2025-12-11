package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionAssignationFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionAssignationResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionUnAssignation;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionValidationFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionValidationResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.service.SeasonTicketSessionsService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

@RestController
@Validated
@RequestMapping(SeasonTicketSessionsController.BASE_URI)
public class SeasonTicketSessionsController {

    public static final String BASE_URI = SeasonTicketController.BASE_URI + "/{seasonTicketId}/sessions";

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS_SESSIONS";

    private SeasonTicketSessionsService seasonTicketSessionsService;

    @Autowired
    public SeasonTicketSessionsController(SeasonTicketSessionsService seasonTicketSessionsService) {
        this.seasonTicketSessionsService = seasonTicketSessionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonTicketSessionsResponse getSessions(@BindUsingJackson SeasonTicketSessionsSearchFilter filter, @PathVariable Long seasonTicketId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        checkParams(seasonTicketId);
        return seasonTicketSessionsService.getSessions(filter, seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            value = "/events",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonTicketSessionsEventsResponse getEventList(@PathVariable Long seasonTicketId, @BindUsingJackson @Valid SeasonTicketSessionsEventsFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        checkParams(seasonTicketId);
        return seasonTicketSessionsService.getEventList(seasonTicketId, filter);
    }

    private void checkParams(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId < 1) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Invalid seasonTicketId", null);
        }
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonTicketSessionAssignationResponse assignSessions(@PathVariable Long seasonTicketId, @RequestBody SeasonTicketSessionAssignationFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        checkParams(seasonTicketId);
        return seasonTicketSessionsService.assignationProcess(seasonTicketId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/{sessionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonTicketSessionUnAssignation unAssignSession(@PathVariable Long seasonTicketId, @PathVariable Long sessionId,
                                                            @RequestParam(value = "update_barcodes", required = false) Boolean updateBarcodes) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        checkParams(seasonTicketId);
        if (sessionId == null || sessionId < 1) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Invalid sessionId", null);
        }
        return seasonTicketSessionsService.unAssignProcess(seasonTicketId, sessionId, updateBarcodes);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/update-barcodes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBarcodes(@PathVariable Long seasonTicketId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_REFRESH);
        seasonTicketSessionsService.updateBarcodes(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/validations")
    public SeasonTicketSessionValidationResponse verifySessions(@PathVariable Long seasonTicketId, @BindUsingJackson @Valid SeasonTicketSessionValidationFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        checkParams(seasonTicketId);
        return seasonTicketSessionsService.verifySessions(seasonTicketId, filter);
    }
}