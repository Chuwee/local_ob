package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_CREATE;

@Validated
@RestController
@RequestMapping(value = SessionMappingController.BASE_URI)
public class SessionMappingController {

    private static final String AUDIT_COLLECTION = "SESSION_MAPPING";
    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}";
    private final SessionMappingService sessionMappingService;

    public SessionMappingController(SessionMappingService sessionMappingService) {
        this.sessionMappingService = sessionMappingService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/mapping")
    public void mapCapacity(@PathVariable Long eventId,
                            @PathVariable Long sessionId,
                            @RequestParam(value = "mapping_tickets", required = false) Boolean mappingTickets,
                            @RequestParam(value = "mapping_full", required = false) Boolean mappingFull ) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_CREATE);
        ConverterUtils.checkField(eventId, "eventId");
        ConverterUtils.checkField(sessionId, "sessionId");
        sessionMappingService.mapCapacity(eventId, sessionId, mappingTickets, mappingFull);
    }
}
