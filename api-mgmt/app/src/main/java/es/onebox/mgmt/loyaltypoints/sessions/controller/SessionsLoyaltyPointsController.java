package es.onebox.mgmt.loyaltypoints.sessions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.loyaltypoints.sessions.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.sessions.dto.UpdateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.sessions.service.SessionsLoyaltyPointsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SessionsLoyaltyPointsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionsLoyaltyPointsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/loyalty-points";

    private static final String AUDIT_COLLECTION = "SESSIONS";

    private final SessionsLoyaltyPointsService sessionsLoyaltyPointsService;

    @Autowired
    public SessionsLoyaltyPointsController(SessionsLoyaltyPointsService sessionsLoyaltyPointsService) {
        this.sessionsLoyaltyPointsService = sessionsLoyaltyPointsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public LoyaltyPointsConfigDTO getLoyaltyPointsConfig(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        return sessionsLoyaltyPointsService.getLoyaltyPointsConfig(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLoyaltyPointsConfig(@PathVariable Long eventId, @PathVariable Long sessionId,
                                          @Valid @RequestBody UpdateLoyaltyPointsConfigDTO loyaltyPointsConfig) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionsLoyaltyPointsService.updateLoyaltyPointsConfig(eventId, sessionId, loyaltyPointsConfig);
    }
}
