package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.accesscontrol.dto.BarcodesDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SessionExternalBarcodesController.BASE_URI)
public class SessionExternalBarcodesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/external-barcodes";
    private static final String AUDIT_COLLECTION = "SESSION_EXTERNAL_BARCODES";

    private final SessionExternalBarcodesService sessionExternalBarcodesService;

    @Autowired
    public SessionExternalBarcodesController(SessionExternalBarcodesService sessionExternalBarcodesService) {
        this.sessionExternalBarcodesService = sessionExternalBarcodesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ExternalBarcodesResponseDTO getExternalBarcodes(@PathVariable Long eventId,
                                                           @PathVariable Long sessionId,
                                                           @RequestParam(value = "limit", required = false, defaultValue = "50") Long limit,
                                                           @RequestParam(value = "offset", required = false, defaultValue = "0") Long offset,
                                                           @RequestParam(value = "barcode", required = false) String barcode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionExternalBarcodesService.getExternalBarcodes(eventId, sessionId, barcode, limit, offset);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, value = "/import")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public IdDTO importExternalBarcodes(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                        @PathVariable @Min(value = 1, message = "sessionId must be above 0") Long sessionId,
                                        @Valid @RequestBody BarcodesDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_IMPORT);
        return sessionExternalBarcodesService.uploadExternalBarcodes(eventId, sessionId, request);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(value = "/import/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public IdDTO externalBarcodesImportStatus(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                               @PathVariable @Min(value = 1, message = "sessionId must be above 0") Long sessionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return sessionExternalBarcodesService.getPendingUpload(eventId, sessionId);
    }

}
