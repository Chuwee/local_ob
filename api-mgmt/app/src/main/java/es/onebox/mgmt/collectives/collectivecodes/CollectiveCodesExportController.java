package es.onebox.mgmt.collectives.collectivecodes;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesExportRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesSearchRequest;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
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

import static es.onebox.core.security.Roles.Codes.ROLE_COL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/collectives/{collectiveId}/codes/exports")
public class CollectiveCodesExportController {

    private static final String AUDIT_EXPORT_COLLECTIVE_CODES = "EXPORT_COLLECTIVE_CODES";
    private static final String AUDIT_EXPORT_COLLECTIVE_CODES_STATUS = "EXPORT_COLLECTIVE_CODES_STATUS";

    private final ExportService exportService;


    @Autowired
    public CollectiveCodesExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long collectiveId,
                                 @Valid @BindUsingJackson CollectiveCodesSearchRequest filter,
                                 @Valid @RequestBody CollectiveCodesExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_COLLECTIVE_CODES, AuditTag.AUDIT_ACTION_EXPORT);

        return exportService.exportCollectiveCodes(collectiveId, filter, body);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable Long collectiveId, @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_COLLECTIVE_CODES_STATUS, AuditTag.AUDIT_ACTION_GET);

        return exportService.checkCollectiveCodesStatus(collectiveId, exportId);
    }

}
