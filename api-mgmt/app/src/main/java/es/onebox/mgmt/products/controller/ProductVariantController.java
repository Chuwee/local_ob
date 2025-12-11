package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductVariantDTO;
import es.onebox.mgmt.products.dto.ProductVariantsDTO;
import es.onebox.mgmt.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantPricesDTO;
import es.onebox.mgmt.products.service.ProductVariantService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductVariantController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductVariantController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/variants";

    private static final String AUDIT_COLLECTION = "PRODUCTS_VARIANT";

    private final ProductVariantService productVariantService;

    @Autowired
    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductVariantsDTO searchProductVariants(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                    @BindUsingJackson @Valid SearchProductVariantsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productVariantService.searchProductVariants(productId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/prices", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductVariantPrices
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @Valid @RequestBody UpdateProductVariantPricesDTO updateProductVariantPricesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        productVariantService.updateProductVariantPrices(productId, updateProductVariantPricesDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{variantId}")
    public ProductVariantDTO getProductVariant
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "variantId must be above 0") Long variantId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return productVariantService.getProductVariant(productId, variantId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/{variantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductVariant
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "variantId must be above 0") Long variantId,
             @Valid @RequestBody UpdateProductVariantDTO updateProductVariantDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        productVariantService.updateProductVariant(productId, variantId, updateProductVariantDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    public ResponseEntity<List<IdNameDTO>> createProductVariants
            (@Min(value = 1, message = "productId must be above 0")
             @PathVariable @Min(value = 1, message = "variantId must be above 0") Long productId) {
        List<IdNameDTO> productVariants = productVariantService.createVariantProductVariants(productId);

        return new ResponseEntity<>(productVariants, HttpStatus.CREATED);
    }
}
