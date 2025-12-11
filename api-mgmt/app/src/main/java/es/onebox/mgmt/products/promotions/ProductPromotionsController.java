package es.onebox.mgmt.products.promotions;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.promotions.dto.CreateProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsFilter;
import es.onebox.mgmt.products.promotions.dto.UpdateProductPromotionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

import java.util.Optional;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductPromotionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductPromotionsController {

    protected static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/promotions";
    private static final String AUDIT_COLLECTION = "PRODUCT_PROMOTIONS";

    private final ProductPromotionsService productPromotionsService;

    @Autowired
    public ProductPromotionsController(ProductPromotionsService productPromotionsService) {
        this.productPromotionsService = productPromotionsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductPromotionsDTO getProductPromotions(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                     @BindUsingJackson @Valid ProductPromotionsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productPromotionsService.getProductPromotions(productId, Optional.ofNullable(filter).orElse(new ProductPromotionsFilter()));
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{promotionId}")
    public ProductPromotionDTO getProductPromotion(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                   @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productPromotionsService.getProductPromotion(productId, promotionId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public IdDTO createProductPromotion(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                        @Valid @RequestBody CreateProductPromotionDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return productPromotionsService.createProductPromotion(productId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductPromotion(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
                                       @RequestBody @Valid UpdateProductPromotionDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productPromotionsService.updateProductPromotion(productId, promotionId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductPromotion(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        productPromotionsService.deleteProductPromotion(productId, promotionId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{promotionId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return productPromotionsService.cloneProductPromotion(productId, promotionId);
    }

}
