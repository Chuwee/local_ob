package es.onebox.mgmt.products.surcharges;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.products.surcharges.dto.ProductSurchargeDTO;
import es.onebox.mgmt.products.surcharges.dto.ProductSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.controller.ProductController;
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
@RequestMapping(value = ProductSurchargeController.BASE_URI)
public class ProductSurchargeController {

    public static final String BASE_URI = ProductController.BASE_URI + "/{productId}/surcharges";
    private static final String AUDIT_COLLECTION = "PRODUCT_SURCHARGES";

    private final ProductSurchargeService productSurchargeService;

    @Autowired
    public ProductSurchargeController(ProductSurchargeService productSurchargeService) {
        this.productSurchargeService = productSurchargeService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping()
    public List<ProductSurchargeDTO> getSurcharges(@PathVariable Long productId,
                                                   @RequestParam(value = "types", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return productSurchargeService.getSurcharges(productId, types);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSurcharge(@PathVariable Long productId, @RequestBody ProductSurchargeListDTO productSurchargeListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        productSurchargeService.setSurcharge(productId, productSurchargeListDTO);
    }
}
