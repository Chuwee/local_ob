package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductPublishingSessionsDTO;
import es.onebox.mgmt.products.dto.ProductSessionSearchFilterDTO;
import es.onebox.mgmt.products.dto.ProductSessionsDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionsDTO;
import es.onebox.mgmt.products.service.ProductSessionService;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = ProductSessionController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSessionController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}";

    private static final String AUDIT_COLLECTION_PRODUCT_SESSIONS = "PRODUCT_SESSIONS";

    private final ProductSessionService productSessionService;

    @Autowired
    public ProductSessionController(ProductSessionService productSessionService) {
        this.productSessionService = productSessionService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/publishing-sessions")
    public ProductPublishingSessionsDTO getProductPublishingSessions(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                           @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRODUCT_SESSIONS, AuditTag.AUDIT_ACTION_GET);

        return productSessionService.getPublishingSessions(productId, eventId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/publishing-sessions")
    public void updateProductSessions(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @RequestBody @Valid UpdateProductSessionsDTO updateProductSessionsDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRODUCT_SESSIONS, AuditTag.AUDIT_ACTION_UPDATE);

        productSessionService.updatePublishingSessions(productId, eventId, updateProductSessionsDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/sessions")
    public ProductSessionsDTO getProductSessions(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                 @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                 @BindUsingJackson @Valid ProductSessionSearchFilterDTO filterDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRODUCT_SESSIONS, AuditTag.AUDIT_ACTION_GET);

        return productSessionService.getProductSessions(productId, eventId, filterDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/sessions/{sessionId}")
    public void updateProductSessions(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "sessionId must be above 0") Long sessionId,
            @RequestBody @Valid UpdateProductSessionDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRODUCT_SESSIONS, AuditTag.AUDIT_ACTION_UPDATE);

        productSessionService.updateProductSession(productId, eventId, sessionId, request);
    }
}
