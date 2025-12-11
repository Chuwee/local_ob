package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesExportRequestDTO;
import es.onebox.mgmt.sessions.dto.WhiteListExportRequest;
import es.onebox.mgmt.validation.ValidationService;
import jakarta.validation.Valid;
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

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}")
public class SessionBarcodesExportController {

    private static final String AUDIT_EXPORT_SESSION_WHITELIST = "EXPORT_SESSION_WHITELIST";
    private static final String AUDIT_EXPORT_SESSION_EXTERNAL_BARCODES = "EXPORT_SESSION_EXTERNAL_BARCODES";
    private static final String AUDIT_EXPORT_SESSION_WHITELIST_STATUS = "EXPORT_SESSION_WHITELIST_STATUS";
    private static final String AUDIT_EXPORT_SESSION_EXTERNAL_BARCODES_STATUS = "EXPORT_SESSION_EXTERNAL_BARCODES_STATUS";

    private final ExportService exportService;
    private final ValidationService validationService;


    @Autowired
    public SessionBarcodesExportController(ExportService exportService, ValidationService validationService) {
        this.exportService = exportService;
        this.validationService = validationService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.POST, value = "/whitelist/exports")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long eventId, @PathVariable Long sessionId,
            @Valid @RequestBody WhiteListExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_WHITELIST, AuditTag.AUDIT_ACTION_EXPORT);
        this.validationService.getAndCheckSession(eventId, sessionId);
        return this.exportService.exportSessionWhiteList(sessionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/whitelist/exports/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable Long eventId, @PathVariable Long sessionId, @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_WHITELIST_STATUS, AuditTag.AUDIT_ACTION_GET);

        this.validationService.getAndCheckSession(eventId, sessionId);
        return exportService.checkSessionWhitelistStatus(sessionId, exportId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.POST, value = "/external-barcodes/exports")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportExternalBarcodes(@PathVariable Long eventId, @PathVariable Long sessionId,
                                 @Valid @RequestBody ExternalBarcodesExportRequestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_EXTERNAL_BARCODES, AuditTag.AUDIT_ACTION_EXPORT);
        this.validationService.getAndCheckSession(eventId, sessionId);
        return this.exportService.exportExternalBarcodes(eventId, sessionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/external-barcodes/exports/{exportId}")
    public ExportStatusResponse getExportExternalBarcodesInfo(@PathVariable Long eventId, @PathVariable Long sessionId, @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_EXTERNAL_BARCODES_STATUS, AuditTag.AUDIT_ACTION_GET);
        this.validationService.getAndCheckSession(eventId, sessionId);
        return exportService.getExternalBarcodesStatus(exportId);
    }

}
