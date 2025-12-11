package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsUpdateDTO;
import jakarta.validation.constraints.NotNull;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = SessionRefundConditionsController.BASE_URI)
@Validated
public class SessionRefundConditionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/refund-conditions";

    public static final String EVENT_ID = "event_id";
    public static final String SESSION_ID = "session_id";
    private static final String AUDIT_COLLECTION = "SESSION_REFUND_CONDITIONS";

    @Autowired
    private SessionRefundConditionsService service;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public SessionRefundConditionsDTO getSessionPriceTypes(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        ConverterUtils.checkField(eventId, EVENT_ID);
        ConverterUtils.checkField(sessionId, SESSION_ID);

        return service.getSessionRefundConditions(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionRefundConditions(@PathVariable Long eventId, @PathVariable Long sessionId,
                                              @RequestBody @NotNull SessionRefundConditionsUpdateDTO updateRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ConverterUtils.checkField(eventId, EVENT_ID);
        ConverterUtils.checkField(sessionId, SESSION_ID);

        service.updateSessionRefundConditions(eventId, sessionId, updateRequest);
    }
}
