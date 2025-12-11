package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.CreateProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValuesDTO;
import es.onebox.mgmt.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeValueDTO;
import es.onebox.mgmt.products.service.ProductAttributeValueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = ProductAttributeValueController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductAttributeValueController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/attributes/{attributeId}/values";

    private static final String AUDIT_COLLECTION = "PRODUCT-ATTRIBUTE-VALUES";

    private final ProductAttributeValueService productAttributeValueService;

    @Autowired
    public ProductAttributeValueController(ProductAttributeValueService productAttributeValueService) {
        this.productAttributeValueService = productAttributeValueService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createProductAttributeValue
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
             @Valid @RequestBody CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long productAttributeId = productAttributeValueService.createProductAttributeValue(productId, attributeId,
                createProductAttributeValueDTO);
        return new ResponseEntity<>(new IdDTO(productAttributeId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{valueId}")
    public ProductAttributeValueDTO getProductAttributeValue
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
             @PathVariable @Min(value = 1, message = "valueId must be above 0") Long valueId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productAttributeValueService.getProductAttributeValue(productId, attributeId, valueId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductAttributeValuesDTO getProductAttributeValues
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
             @BindUsingJackson @Valid SearchProductAttributeValueFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productAttributeValueService.getProductAttributeValues(productId, attributeId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/{valueId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductAttributeValue(@PathVariable @Min(value = 1, message = "productId must be above 0") Long
                                                    productId,
                                            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
                                            @PathVariable @Min(value = 1, message = "valueId must be above 0") Long valueId,
                                            @Valid @RequestBody UpdateProductAttributeValueDTO updateProductAttributeValueDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productAttributeValueService.updateProductAttributeValue(productId, attributeId, valueId, updateProductAttributeValueDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{valueId}")
    public void deleteProductAttributeValue(@PathVariable @Min(value = 1, message = "productId must be above 0") Long
                                                    productId,
                                            @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
                                            @PathVariable @Min(value = 1, message = "valueId must be above 0") Long valueId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        productAttributeValueService.deleteProductAttributeValue(productId, attributeId, valueId);
    }
}
