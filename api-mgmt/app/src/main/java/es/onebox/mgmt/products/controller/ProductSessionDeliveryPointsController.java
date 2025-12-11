package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.products.service.ProductSessionDeliveryPointsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductSessionDeliveryPointsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSessionDeliveryPointsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}/session-delivery-points";

    private static final String AUDIT_COLLECTION = "PRODUCTS-SESSIONS-DELIVERY-POINTS";

    private final ProductSessionDeliveryPointsService productSessionDeliveryPointsService;

    @Autowired
    public ProductSessionDeliveryPointsController(ProductSessionDeliveryPointsService productSessionDeliveryPointsService) {
        this.productSessionDeliveryPointsService = productSessionDeliveryPointsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductSessionDeliveryPointsDTO getProductSessionDeliveryPoints(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                           @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                           @BindUsingJackson @Valid ProductSessionDeliveryPointsFilterDTO filterDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productSessionDeliveryPointsService.getProductSessionDeliveryPoints(productId, eventId, filterDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductSessionDeliveryPointsDTO> changeProductSessionDeliveryPoints(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                                              @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                                              @Valid @RequestBody UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointDTOS) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ProductSessionDeliveryPointsDTO productSessionDeliveryPointDTOS = productSessionDeliveryPointsService.updateProductSessionDeliveryPoints(productId, eventId, updateProductSessionDeliveryPointDTOS);
        return new ResponseEntity<>(productSessionDeliveryPointDTOS, HttpStatus.OK);
    }

}
