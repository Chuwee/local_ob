package es.onebox.mgmt.salerequests.commissions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionListDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.commissions.service.SaleRequestCommissionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(
        value = SaleRequestCommissionsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestCommissionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/commissions";

    private static final String AUDIT_COLLECTION = "SALES_REQUEST";

    private final SaleRequestCommissionsService commissionsService;

    @Autowired
    public SaleRequestCommissionsController(SaleRequestCommissionsService commissionsService) {
        this.commissionsService = commissionsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ResponseEntity<List<CommissionDTO>> commissions(@PathVariable Long saleRequestId,
                                                           @Valid @RequestParam(value = "type", required = false) List<CommissionTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        List<CommissionDTO> commissions = commissionsService.saleRequestCommissions(saleRequestId, types);
        return new ResponseEntity<>(commissions, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updateCommissions(@PathVariable Long saleRequestId,
                                 @Valid @RequestBody CommissionListDTO commissionListDto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        commissionsService.updateSaleRequestCommissions(saleRequestId, commissionListDto);
    }
}
