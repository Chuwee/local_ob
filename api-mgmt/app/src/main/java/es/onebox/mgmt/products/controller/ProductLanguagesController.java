package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductLanguagesDTO;
import es.onebox.mgmt.products.dto.UpdateProductLanguagesDTO;
import es.onebox.mgmt.products.service.ProductLanguagesService;
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
@RequestMapping(value = ProductLanguagesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductLanguagesController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/languages";

    private static final String AUDIT_COLLECTION = "PRODUCTS-LANGUAGES";

    private final ProductLanguagesService productLanguagesService;

    @Autowired
    public ProductLanguagesController(ProductLanguagesService productLanguagesService) {
        this.productLanguagesService = productLanguagesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductLanguagesDTO getProductLanguages(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productLanguagesService.getProductLanguages(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductLanguagesDTO> changeProductLanguages(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId, @Valid @RequestBody UpdateProductLanguagesDTO updateProductLanguagesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ProductLanguagesDTO productLanguagesDTO = productLanguagesService.updateProductLanguages(productId, updateProductLanguagesDTO);
        return new ResponseEntity<>(productLanguagesDTO, HttpStatus.OK);
    }

}
