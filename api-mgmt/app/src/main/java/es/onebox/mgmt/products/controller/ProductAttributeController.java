package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.CreateProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributesDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeDTO;
import es.onebox.mgmt.products.service.ProductAttributeService;
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
@RequestMapping(value = ProductAttributeController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductAttributeController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/attributes";

    private static final String AUDIT_COLLECTION = "PRODUCT-ATTRIBUTES";

    private final ProductAttributeService productAttributeService;

    @Autowired
    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createProductAttribute
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @Valid @RequestBody CreateProductAttributeDTO createProductAttributeDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long productAttributeId = productAttributeService.createProductAttribute(productId, createProductAttributeDTO);
        return new ResponseEntity<>(new IdDTO(productAttributeId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{attributeId}")
    public ProductAttributeDTO getProductAttribute
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
             @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productAttributeService.getProductAttribute(productId, attributeId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductAttributesDTO getProductAttributes
            (@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productAttributeService.getProductAttributes(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/{attributeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductAttribute(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                              @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId,
                              @Valid @RequestBody UpdateProductAttributeDTO updateProductAttributeDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productAttributeService.updateProductAttribute(productId, attributeId, updateProductAttributeDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{attributeId}")
    public void deleteProductAttribute(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                              @PathVariable @Min(value = 1, message = "attributeId must be above 0") Long attributeId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        productAttributeService.deleteProductAttribute(productId, attributeId);
    }
}
