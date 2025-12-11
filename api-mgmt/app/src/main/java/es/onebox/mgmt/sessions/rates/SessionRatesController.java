package es.onebox.mgmt.sessions.rates;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.sessions.dto.SessionRateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SessionRatesController.BASE_URI)
public class SessionRatesController {

    private static final String AUDIT_COLLECTION = "SESSION_RATES";
    private static final String AUDIT_SUBCOLLECTION_RESTRICTIONS = "RESTRICTIONS";
    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/rates";
    private static final String RESTRICTIONS = "/restrictions";
    private static final String RATE_ID = "/{rateId}";
    private static final String RATE_RESTRICTIONS = RATE_ID + RESTRICTIONS;
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String RATE_ID_MUST_BE_ABOVE_0 = "Rate Id must be above 0";

    private final SessionRatesService sessionRatesService;

    public SessionRatesController(SessionRatesService sessionRatesService) {
        this.sessionRatesService = sessionRatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<SessionRateDTO> getSessionRates(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return sessionRatesService.getRates(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RESTRICTIONS)
    public RatesRestrictedDTO getRestrictedRates(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId) {

        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionRatesService.getRestrictedRates(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> upsertEventRateRestrictions(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId,
            @RequestBody @Valid RateRestrictionDTO restrictionDTO) {

        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_UPDATE);
        sessionRatesService.upsertSessionRatesRestrictions(eventId, sessionId, rateId, restrictionDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> deleteEventRateRestrictions(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId) {

        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_DELETE);
        sessionRatesService.deleteSessionRate(eventId, sessionId, rateId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
