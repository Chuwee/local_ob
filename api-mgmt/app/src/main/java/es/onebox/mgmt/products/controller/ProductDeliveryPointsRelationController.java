package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.mgmt.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.mgmt.products.service.ProductDeliveryPointRelationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductDeliveryPointsRelationController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductDeliveryPointsRelationController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/delivery-points";

    private static final String AUDIT_COLLECTION = "PRODUCTS-DELIVERY-POINTS_RELATIONS";

    private final ProductDeliveryPointRelationService productDeliveryPointRelationService;

    @Autowired
    public ProductDeliveryPointsRelationController(ProductDeliveryPointRelationService productDeliveryPointRelationService) {
        this.productDeliveryPointRelationService = productDeliveryPointRelationService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void upsertProductDeliveryPointRelation(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                   @Valid @RequestBody UpsertProductDeliveryPointRelationDTO upsertProductDeliveryPointRelationDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        productDeliveryPointRelationService.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelationDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{deliveryPointId}")
    public ProductDeliveryPointRelationDTO getProductDeliveryPointRelation(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                           @PathVariable @Min(value = 1, message = "deliveryPointId must be above 0") Long deliveryPointId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productDeliveryPointRelationService.getProductDeliveryPointRelation(productId, deliveryPointId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductDeliveryPointsRelationsDTO searchProductDeliveryPointRelation(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                                @BindUsingJackson @Valid SearchProductDeliveryPointRelationFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return productDeliveryPointRelationService.searchProductDeliveryPointRelation(productId, filter);
    }

}
