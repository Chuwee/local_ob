package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.dto.CloneSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.LinkedSessionDTO;
import es.onebox.mgmt.sessions.dto.SearchSessionsResponse;
import es.onebox.mgmt.sessions.dto.SessionAdditionalConfigDTO;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionDTO;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionsGroupsDTO;
import es.onebox.mgmt.sessions.dto.SessionsGroupsSearchFilter;
import es.onebox.mgmt.sessions.dto.TierQuotaAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionResponseDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionsRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SessionsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions";

    private static final String AUDIT_COLLECTION = "SESSIONS";
    private static final String FIELD_EVENT_ID = "eventId";
    private static final String FIELD_SESSION_ID = "sessionId";

    private final SessionsService sessionService;

    @Autowired
    public SessionsController(SessionsService sessionService) {
        this.sessionService = sessionService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}")
    public SessionDTO getSession(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        checkEventAndSessionIds(eventId, sessionId);
        return sessionService.getSession(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/additional-config")
    public SessionAdditionalConfigDTO getSessionAdditionalConfig(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        checkEventAndSessionIds(eventId, sessionId);
        return sessionService.getSessionAdditionalConfig(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/linked-sessions")
    public ResponseEntity<List<LinkedSessionDTO>> getLinkedSessions(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        checkEventAndSessionIds(eventId, sessionId);
        List<LinkedSessionDTO> linkedSessions = sessionService.getLinkedSessions(eventId, sessionId);
        return ResponseEntity.ok(linkedSessions);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public SearchSessionsResponse getSessions(@PathVariable Long eventId, @BindUsingJackson @Valid SessionSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return sessionService.searchSessions(eventId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createSession(@PathVariable Long eventId,
                                               @Valid @RequestBody CreateSessionRequestDTO sessionData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long sessionId = sessionService.createSession(eventId, sessionData);
        return new ResponseEntity<>(new IdDTO(sessionId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/groups")
    public SessionsGroupsDTO getSessionsGroups(@PathVariable Long eventId, @BindUsingJackson @Valid SessionsGroupsSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return sessionService.searchSessionsGroups(eventId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(path = "/bulk",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> createSessions(@PathVariable Long eventId,
                                                     @Valid @RequestBody CreateSessionRequestDTO[] sessionData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        List<Long> sessionIds = sessionService.createSessions(eventId, Arrays.asList(sessionData));

        return new ResponseEntity<>(sessionIds, HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(path = "/{sessionId}/clone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> cloneSession(@PathVariable Long eventId, @PathVariable Long sessionId,
                                              @Valid @BindUsingJackson @RequestBody CloneSessionRequestDTO sessionData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);

        Long newSessionId = sessionService.cloneSession(eventId, sessionId, sessionData);
        return new ResponseEntity<>(new IdDTO(newSessionId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(path = "/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSession(@PathVariable Long eventId, @PathVariable Long sessionId,
                              @Valid @RequestBody UpdateSessionRequestDTO sessionData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (sessionData.getId() != null && !sessionData.getId().equals(sessionId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "sessionId is different between pathVariable and requestBody", null);
        }
        sessionService.updateSession(eventId, sessionId, sessionData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(path = "/bulk",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UpdateSessionResponseDTO>> updateSessions(@PathVariable Long eventId,
                                                                         @RequestParam(value = "preview", required = false) Boolean preview,
                                                                         @Valid @RequestBody UpdateSessionsRequestDTO sessionsData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        List<UpdateSessionResponseDTO> updated = sessionService.updateSessions(eventId, sessionsData, preview);

        return ResponseEntity.ok(updated);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long eventId, @PathVariable Long sessionId,
                              @RequestParam(value = "pack_related_sessions_seats", required = false) String relatedSeats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        checkEventAndSessionIds(eventId, sessionId);
        sessionService.delete(eventId, sessionId, relatedSeats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("bulk")
    public ResponseEntity<List<UpdateSessionResponseDTO>> deleteSessions(@PathVariable Long eventId,
                                                                         @RequestParam(value = "preview", required = false) Boolean preview,
                                                                         @RequestParam(value = "ids") @NotEmpty(message = "Session ids are mandatories") Long[] ids) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        List<UpdateSessionResponseDTO> deleted = sessionService.deleteSessions(eventId, Arrays.asList(ids), preview);
        return ResponseEntity.ok(deleted);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/availability")
    public SessionAvailabilityDTO getAvailability(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        checkEventAndSessionIds(eventId, sessionId);
        return sessionService.getSessionAvailability(eventId, sessionId);
    }


    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/availability/price-types")
    public List<SessionPriceTypesAvailabilityDTO> getPriceTypesAvailability(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        checkEventAndSessionIds(eventId, sessionId);
        return sessionService.getSessionPriceTypesAvailability(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/availability/tiers")
    public List<TierQuotaAvailabilityDTO> getTiersAvailability(@PathVariable(value = "eventId") Long eventId,
                                                               @PathVariable(value = "sessionId") Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        checkEventAndSessionIds(eventId, sessionId);
        return sessionService.getTiersAvailability(eventId, sessionId);
    }

    private static void checkEventAndSessionIds(Long eventId, Long sessionId) {
        ConverterUtils.checkField(eventId, FIELD_EVENT_ID);
        ConverterUtils.checkField(sessionId, FIELD_SESSION_ID);
    }

}
