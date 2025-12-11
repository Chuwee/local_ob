package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.sessions.dto.CapacityExportRequest;
import es.onebox.mgmt.validation.ValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/capacity/exports")
public class SessionCapacityExportController {

    private static final String AUDIT_EXPORT_SESSION_CAPACITY = "EXPORT_SESSION_WHITELIST";
    private static final String AUDIT_EXPORT_SESSION_CAPACITY_STATUS = "EXPORT_SESSION_WHITELIST_STATUS";

    private final ExportService exportService;
    private final ValidationService validationService;


    @Autowired
    public SessionCapacityExportController(ExportService exportService, ValidationService validationService) {
        this.exportService = exportService;
        this.validationService = validationService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long eventId, @PathVariable Long sessionId,
                                 @Valid @RequestBody CapacityExportRequest body,
                                 @RequestParam(value = "sector_ids", required = false) List<Long> sectorIds,
                                 @RequestParam(value = "view_ids", required = false) List<Long> viewIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_CAPACITY, AuditTag.AUDIT_ACTION_EXPORT);
        Session session = this.validationService.getAndCheckSession(eventId, sessionId);
        return this.exportService.exportSessionCapacity(eventId, sessionId, session.getVenueConfigId(), body, viewIds, sectorIds);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable Long eventId, @PathVariable Long sessionId, @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_SESSION_CAPACITY_STATUS, AuditTag.AUDIT_ACTION_GET);

        this.validationService.getAndCheckSession(eventId, sessionId);
        return exportService.checkSessionCapacityStatus(eventId, sessionId, exportId);
    }

}
