package es.onebox.mgmt.packsalerequest;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.packsalerequest.dto.request.PackSaleRequestUpdateStatusDTO;
import es.onebox.mgmt.packsalerequest.dto.request.PackSaleRequestsSearchFilterDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestBaseResponseDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(PackSaleRequestController.BASE_URI)
public class PackSaleRequestController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs-sale-requests";

    private static final String AUDIT_COLLECTION = "PACK_SALE_REQUESTS";

    private final PackSaleRequestService packSaleRequestService;

    public PackSaleRequestController(PackSaleRequestService packSaleRequestService) {
        this.packSaleRequestService = packSaleRequestService;
    }

    @GetMapping
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public PackSaleRequestResponseDTO search(PackSaleRequestsSearchFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return packSaleRequestService.search(filter);
    }

    @GetMapping("/{saleRequestId}")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public PackSaleRequestBaseResponseDTO getDetail(Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return packSaleRequestService.getDetail(saleRequestId);
    }

    @PutMapping("/{saleRequestId}/status")
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Long saleRequestId, @RequestBody PackSaleRequestUpdateStatusDTO updateStatus) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packSaleRequestService.updateStatus(saleRequestId, updateStatus);
    }

}
