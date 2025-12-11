package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductLiteralsDTO;
import es.onebox.mgmt.products.dto.ProductTicketLiteralsDTO;
import es.onebox.mgmt.products.dto.ProductValueLiteralsDTO;
import es.onebox.mgmt.products.service.ProductContentsService;
import es.onebox.mgmt.products.service.ProductTicketContentsService;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ProductContentsController.BASE_URI)
public class ProductContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}";

    private static final String AUDIT_COLLECTION = "PRODUCT_CONTENTS";

    private final ProductContentsService productContentsService;
    private final ProductTicketContentsService productTicketContentsService;

    @Autowired
    public ProductContentsController(ProductContentsService productContentsService,
                                     ProductTicketContentsService productTicketContentsService) {
        this.productContentsService = productContentsService;
        this.productTicketContentsService = productTicketContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/attributes/{attributeId}/channel-contents/texts")
    public ProductLiteralsDTO getProductAttributeLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @RequestParam(required = false) @LanguageIETF String language) {
        addAuditTag(AuditTag.AUDIT_ACTION_GET);
        return this.productContentsService.getProductAttributeLiterals(productId, attributeId, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/attributes/{attributeId}/channel-contents/texts")
    public void upsertProductAttributeLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @RequestBody @NotEmpty @Valid ProductLiteralsDTO body) {
        addAuditTag(AuditTag.AUDIT_ACTION_CREATE);
        this.productContentsService.upsertProductAttributeLiterals(productId, attributeId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/attributes/{attributeId}/values/{valueId}/channel-contents/texts")
    public ProductLiteralsDTO getProductValueLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @PathVariable @Min(value = 1, message = "valueId must be above 0") Long valueId,
            @RequestParam(required = false) @LanguageIETF String language) {
        addAuditTag(AuditTag.AUDIT_ACTION_GET);
        return this.productContentsService.getProductValueLiterals(productId, attributeId, valueId, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/attributes/{attributeId}/values/channel-contents/texts")
    public ProductValueLiteralsDTO getProductBulkValueLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @RequestParam(required = false) @LanguageIETF String language) {
        addAuditTag(AuditTag.AUDIT_ACTION_GET);
        return this.productContentsService.getProductBulkValueLiterals(productId, attributeId, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/attributes/{attributeId}/values/{valueId}/channel-contents/texts")
    public void upsertProductValueLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @PathVariable @Min(value = 1, message = "valueId must be above 0") Long valueId,
            @RequestBody @NotEmpty @Valid ProductLiteralsDTO body) {
        addAuditTag(AuditTag.AUDIT_ACTION_CREATE);
        this.productContentsService.upsertProductValueLiterals(productId, attributeId, valueId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/attributes/{attributeId}/values/channel-contents/texts")
    public void upsertBulkProductValueLiterals(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
            @RequestBody @NotEmpty @Valid ProductValueLiteralsDTO body) {
        addAuditTag(AuditTag.AUDIT_ACTION_CREATE);
        this.productContentsService.upsertBulkProductValueLiterals(productId, attributeId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/ticket-contents/languages/{language}")
    public ProductTicketLiteralsDTO getTicketContents(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @LanguageIETF String language) {
        addAuditTag(AuditTag.AUDIT_ACTION_GET);
        return this.productTicketContentsService.getProductTicketLiterals(productId, language, null);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/ticket-contents/languages/{language}/{key}")
    public ProductTicketLiteralsDTO filterTicketContentsByKey(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @LanguageIETF String language, @PathVariable String key) {
        addAuditTag(AuditTag.AUDIT_ACTION_GET);
        return this.productTicketContentsService.getProductTicketLiterals(productId, language, key);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/ticket-contents/languages/{language}")
    public void upsertTicketContents(
            @PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
            @PathVariable @LanguageIETF String language, @RequestBody @NotEmpty @Valid ProductTicketLiteralsDTO body) {
        addAuditTag(AuditTag.AUDIT_ACTION_CREATE);
        this.productTicketContentsService.upsertProductTicketLiterals(productId, language, body);
    }

    private void addAuditTag(String action) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, action);
    }

}
