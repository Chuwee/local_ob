package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductEventDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDeliveryPointsDTO;
import es.onebox.mgmt.products.service.ProductEventDeliveryPointsService;
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
@RequestMapping(value = ProductEventDeliveryPointsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductEventDeliveryPointsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}/delivery-points";

    private static final String AUDIT_COLLECTION = "PRODUCTS-EVENTS-DELIVERY-POINTS";

    private final ProductEventDeliveryPointsService productEventDeliveryPointsService;

    @Autowired
    public ProductEventDeliveryPointsController(ProductEventDeliveryPointsService productEventDeliveryPointsService) {
        this.productEventDeliveryPointsService = productEventDeliveryPointsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductEventDeliveryPointsDTO getProductEventDeliveryPoints(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                       @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productEventDeliveryPointsService.getProductEventDeliveryPoints(productId, eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductEventDeliveryPointsDTO> changeProductEventDeliveryPoints(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                                @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                                @Valid @RequestBody UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ProductEventDeliveryPointsDTO productEventDeliveryPointDTOS = productEventDeliveryPointsService.updateProductEventDelvieryPoints(productId, eventId, updateProductEventDeliveryPointsDTO);
        return new ResponseEntity<>(productEventDeliveryPointDTOS, HttpStatus.OK);
    }

}
