package es.onebox.mgmt.sessions.presales;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.CreateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionPreSaleDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SessionPresalesController.BASE_URI)
public class SessionPresalesController {

    private static final String AUDIT_COLLECTION = "SESSION_PRESALES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/presales";

    private final SessionPresalesService sessionPresalesService;

    public SessionPresalesController(SessionPresalesService sessionPresalesService) {
        this.sessionPresalesService = sessionPresalesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public List<SessionPreSaleDTO> getSessionPreSale(@PathVariable(value = "eventId") Long eventId,
                                                     @PathVariable(value = "sessionId") Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return sessionPresalesService.getSessionPreSale(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionPreSaleDTO createSessionPreSale(@PathVariable(value = "eventId") Long eventId,
                                                  @PathVariable(value = "sessionId") Long sessionId,
                                                  @Valid @RequestBody CreateSessionPreSaleDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return sessionPresalesService.createSessionPreSale(eventId, sessionId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{presalesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionPreSale(@PathVariable(value = "eventId") Long eventId,
                                     @PathVariable(value = "sessionId") Long sessionId,
                                     @PathVariable(value = "presalesId") Long presalesId,
                                     @Valid @RequestBody UpdateSessionPreSaleDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionPresalesService.updateSessionPreSale(eventId, sessionId, presalesId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{presalesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSessionPreSale(@PathVariable(value = "eventId") Long eventId,
                                     @PathVariable(value = "sessionId") Long sessionId,
                                     @PathVariable(value = "presalesId") Long presalesId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        sessionPresalesService.deleteSessionPreSale(eventId, sessionId, presalesId);
    }
}
