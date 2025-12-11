package es.onebox.mgmt.salerequests.gateways;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.gateways.dto.GatewayConfigUpdateRequestDTO;
import es.onebox.mgmt.salerequests.gateways.dto.SaleRequestGatewayConfigDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(value = SaleRequestGatewaysController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestGatewaysController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/gateways";
    private static final String AUDIT_COLLECTION = "SALE_REQUESTS";

    private SaleRequestGatewaysService gatewaysService;

    @Autowired
    public SaleRequestGatewaysController(SaleRequestGatewaysService gatewaysService){
        this.gatewaysService = gatewaysService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestGatewayConfigDTO getSaleRequestGatewayConfiguration(@PathVariable Long saleRequestId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return gatewaysService.getSaleRequestGatewayConfiguration(saleRequestId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSaleRequestGatewayConfiguration(@PathVariable Long saleRequestId,
                                                      @Valid @RequestBody GatewayConfigUpdateRequestDTO request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        gatewaysService.updateSaleRequestGatewayConfiguration(saleRequestId, request);
    }

}