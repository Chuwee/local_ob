package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.AddProductEventsDTO;
import es.onebox.mgmt.products.dto.ProductEventsDTO;
import es.onebox.mgmt.products.dto.ProductEventsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDTO;
import es.onebox.mgmt.products.service.ProductEventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping(value = ProductEventController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductEventController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events";
    private static final String AUDIT_COLLECTION = "PRODUCT_EVENT";

    private final ProductEventService productEventService;

    @Autowired
    public ProductEventController(ProductEventService productEventService) {
        this.productEventService = productEventService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductEventsDTO addProductEvents(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                             @Valid @RequestBody AddProductEventsDTO addProductEventsDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return productEventService.addProductEvents(productId, addProductEventsDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{eventId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductEvent(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                   @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                   @Valid @RequestBody UpdateProductEventDTO updateProductEventDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        productEventService.updateProductEvent(productId, eventId, updateProductEventDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductEvent(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                         @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        productEventService.deleteProductEvent(productId, eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductEventsDTO getProductEvents(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                             @BindUsingJackson ProductEventsFilterDTO filterDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return productEventService.getProductEvents(productId, filterDTO);
    }
}
