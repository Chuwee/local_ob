package es.onebox.mgmt.salerequests.delivery;


import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.delivery.dto.SaleRequestDeliveryDTO;
import es.onebox.mgmt.salerequests.delivery.dto.UpdateSaleRequestDeliveryDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SaleRequestDeliveryController.BASE_URI)
public class SaleRequestDeliveryController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/delivery";
    private static final String AUDIT_COLLECTION = "SALES_REQUEST_DELIVERY";

    private final SaleRequestDeliveryService deliveryService;

    @Autowired
    public SaleRequestDeliveryController(SaleRequestDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestDeliveryDTO getDelivery(@PathVariable Long saleRequestId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return deliveryService.getDelivery(saleRequestId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updateDelivery(@PathVariable Long saleRequestId,
                               @Valid @RequestBody UpdateSaleRequestDeliveryDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        deliveryService.updateDelivery(saleRequestId, request);
    }

}
