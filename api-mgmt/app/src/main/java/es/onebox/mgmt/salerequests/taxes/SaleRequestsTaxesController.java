package es.onebox.mgmt.salerequests.taxes;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestSurchargesTaxesDTO;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestsSurchargesTaxesUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = SaleRequestsTaxesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestsTaxesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/surcharges-taxes";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS_SURCHARGES_TAXES";

    private final SaleRequestsTaxesService saleRequestsTaxesService;

    @Autowired
    public SaleRequestsTaxesController(SaleRequestsTaxesService saleRequestsTaxesService) {
        this.saleRequestsTaxesService = saleRequestsTaxesService;
    }
    
    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public SaleRequestSurchargesTaxesDTO getSaleRequestSurchargesTaxes(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return saleRequestsTaxesService.getSaleRequestSurchargesTaxes(saleRequestId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void updateSaleRequestSurchargesTaxes(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
                                                 @Valid @RequestBody SaleRequestsSurchargesTaxesUpdateDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        saleRequestsTaxesService.updateSaleRequestSurchargesTaxes(saleRequestId, body);
    }

}