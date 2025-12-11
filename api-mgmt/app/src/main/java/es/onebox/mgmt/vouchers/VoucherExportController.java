package es.onebox.mgmt.vouchers;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.vouchers.dto.VoucherExportRequest;
import es.onebox.mgmt.vouchers.service.VoucherGroupsService;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/voucher-groups/{voucherGroupId}/vouchers/exports")
public class VoucherExportController {

    private static final String AUDIT_EXPORT_VOUCHERS = "EXPORT_VOUCHERS";
    private static final String AUDIT_EXPORT_VOUCHERS_STATUS = "EXPORT_VOUCHERS_STATUS";

    private final ExportService exportService;
    private final VoucherGroupsService voucherGroupsService;


    @Autowired
    public VoucherExportController(ExportService exportService, VoucherGroupsService voucherGroupsService) {
        this.exportService = exportService;
        this.voucherGroupsService = voucherGroupsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long voucherGroupId, @Valid @RequestBody VoucherExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_VOUCHERS, AuditTag.AUDIT_ACTION_EXPORT);
        this.voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        return this.exportService.exportVouchers(voucherGroupId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable Long voucherGroupId, @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORT_VOUCHERS_STATUS, AuditTag.AUDIT_ACTION_GET);

        this.voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        return exportService.checkVoucherStatus(voucherGroupId, exportId);
    }

}
