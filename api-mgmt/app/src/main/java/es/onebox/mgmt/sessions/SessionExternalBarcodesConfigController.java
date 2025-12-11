package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.SessionExternalBarcodeConfigDTO;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SessionExternalBarcodesConfigController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionExternalBarcodesConfigController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/external-barcodes/config";

    private static final String AUDIT_COLLECTION = "SESSIONS_EXTERNAL_BARCODES";

    private final SessionExternalBarcodesConfigService sessionExternalBarcodesConfigService;

    @Autowired
    public SessionExternalBarcodesConfigController(SessionExternalBarcodesConfigService sessionExternalBarcodesConfigService) {
        this.sessionExternalBarcodesConfigService = sessionExternalBarcodesConfigService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public SessionExternalBarcodeConfigDTO getEventExternalBarcodeConfig(@PathVariable Long eventId, @PathVariable Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionExternalBarcodesConfigService.getSessionExternalBarcodeConfig(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEventExternalBarcodeConfig(@PathVariable Long eventId,
                                              @PathVariable Long sessionId,
                                              @Valid @RequestBody SessionExternalBarcodeConfigDTO externalBarcodesConfig) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionExternalBarcodesConfigService.updateSessionExternalBarcodeConfig(eventId, sessionId, externalBarcodesConfig);
    }

}
