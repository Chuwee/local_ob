package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/ticket-templates")
public class SessionTicketTemplatesController {

    @Autowired
    private SessionTicketTemplatesService sessionTicketTemplatesService;

    private static final String AUDIT_COLLECTION = "SESSION_TICKET_TEMPLATES";

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/passbook/preview")
    public TicketPrintResultDTO getPassbookPreview(@PathVariable Long eventId, @PathVariable Long sessionId,
                                                   @RequestParam(required = false) String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.sessionTicketTemplatesService.getSessionPassbookPreview(eventId, sessionId, language);
    }
}
