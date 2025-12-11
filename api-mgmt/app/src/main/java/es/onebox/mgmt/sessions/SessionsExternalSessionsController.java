package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalSessionConfig;
import es.onebox.mgmt.sessions.dto.UpdateSessionExternalSessionsRequestDTO;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SessionsExternalSessionsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionsExternalSessionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/external-session-config";

    private static final String AUDIT_COLLECTION = "SESSIONS";

    private final SessionsExternalSessionsService sessionService;

    @Autowired
    public SessionsExternalSessionsController(SessionsExternalSessionsService sessionService) {
        this.sessionService = sessionService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public ExternalSessionConfig getSessionExternalSessions(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return sessionService.getSessionExternalSessions(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionExternalSessions(@PathVariable Long eventId, @PathVariable Long sessionId,
                                              @Valid @RequestBody UpdateSessionExternalSessionsRequestDTO sessionData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionService.updateSessionExternalSessions(eventId, sessionId, sessionData);
    }

}
