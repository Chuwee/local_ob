package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.CreateProductContentTextListDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.mgmt.products.service.ProductCommunicationElementsService;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
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
@RequestMapping(value = ProductCommunicationElementsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductCommunicationElementsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/communication-elements";

    public static final String LANGUAGES_URI = "/languages/{language}";
    public static final String TYPES_URI = "/types/{type}";
    public static final String POSITIONS_URI = "/positions/{position}";

    private static final String AUDIT_COLLECTION = "PRODUCTS-COMMUNICATION-ELEMENTS";

    private final ProductCommunicationElementsService productCommunicationElementsService;

    @Autowired
    public ProductCommunicationElementsController(ProductCommunicationElementsService productCommunicationElementsService) {
        this.productCommunicationElementsService = productCommunicationElementsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductCommunicationElementsTextsDTO> createProductCommunicationElementsTexts(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                                                        @Valid @RequestBody CreateProductContentTextListDTO createProductContentTextListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        ProductCommunicationElementsTextsDTO productCommunicationElementsTextsDTO = productCommunicationElementsService.createProductCommunicationElementsText(productId, createProductContentTextListDTO);
        return new ResponseEntity<>(productCommunicationElementsTextsDTO, HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductCommunicationElementsTextsDTO getProductCommunicationElementsTexts(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productCommunicationElementsService.getProductCommunicationElementsText(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/images", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createProductCommunicationElementsImages(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                                                       @Valid @RequestBody CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        productCommunicationElementsService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImagesDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductCommunicationElementsImagesDTO<ProductCommunicationElementsImagesType> getProductCommunicationElementsImages(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productCommunicationElementsService.getProductCommunicationElementsImages(productId);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/images" + LANGUAGES_URI + TYPES_URI + POSITIONS_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicketPDFImageContents(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                             @PathVariable @LanguageIETF String language,
                                             @PathVariable ProductCommunicationElementsImagesType type,
                                             @PathVariable Integer position){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        productCommunicationElementsService.deleteProductCommunicationElementsImages(productId, language, type, position);
    }

}
