package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePdfListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPdfListDTO;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentType;
import es.onebox.mgmt.products.service.ProductTicketContentService;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductTicketContentsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductTicketContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/ticket-contents/";
    public static final String LANGUAGES_URI = "/languages/{language}";
    private static final String PRODUCT_ID_MUST_BE_ABOVE_0 = "Product Id must be above 0";
    private static final String AUDIT_COLLECTION_PDF = "PRODUCT_TICKET_COMMUNICATION_ELEMENTS_PDF";
    private static final String AUDIT_COLLECTION_PASSBOOK = "PRODUCT_TICKET_COMMUNICATION_ELEMENTS_PASSBOOK";

    private final ProductTicketContentService productTicketContentService;

    @Autowired
    public ProductTicketContentsController(ProductTicketContentService productTicketContentService) {
        this.productTicketContentService = productTicketContentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "PDF/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createOrUpdateTicketContentsPdfText(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId,
                                                    @Valid @RequestBody ProductTicketContentTextPdfListDTO ticketContentDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PDF, AuditTag.AUDIT_ACTION_CREATE);

        productTicketContentService.createPdfTexts(productId, ticketContentDTO, ProductTicketContentType.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductTicketContentTextPdfListDTO getTicketContentsPdfTexts(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);

        return productTicketContentService.getPdfTexts(productId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "PDF/images", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createOrUpdateTicketContentsPdfImage(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId,
                                                     @Valid @NotEmpty @RequestBody ProductTicketContentImagePdfListDTO ticketContentDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PDF, AuditTag.AUDIT_ACTION_CREATE);
        productTicketContentService.createPdfImages(productId, ticketContentDTO, ProductTicketContentType.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductTicketContentImagePdfListDTO getTicketContentsPdfImage(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);

        return productTicketContentService.getPdfImages(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "PDF/images" + LANGUAGES_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicketContentsPdfImage(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                             @PathVariable @LanguageIETF String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        productTicketContentService.deletePdfImages(productId, language);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "PASSBOOK/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createOrUpdateTicketContentsPassbookText(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId,
                                                         @Valid @RequestBody ProductTicketContentTextPassbookListDTO ticketContentDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_CREATE);

        productTicketContentService.createPassbookTexts(productId, ticketContentDTO, ProductTicketContentType.PASSBOOK);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "PASSBOOK/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductTicketContentTextPassbookListDTO getTicketContentsPassbookTexts(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);

        return productTicketContentService.getPassbookTexts(productId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "PASSBOOK/images", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createOrUpdateTicketContentsPassbookImage(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId,
                                                          @Valid @RequestBody ProductTicketContentImagePassbookListDTO ticketContentDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_CREATE);
        productTicketContentService.createPassbookImages(productId, ticketContentDTO, ProductTicketContentType.PASSBOOK);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "PASSBOOK/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductTicketContentImagePassbookListDTO getTicketContentsPassbookImage(@PathVariable @Min(value = 1, message = PRODUCT_ID_MUST_BE_ABOVE_0) Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);

        return productTicketContentService.getPassbookImages(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "PASSBOOK/images" + LANGUAGES_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicketContentsPassbookImage(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                                  @PathVariable @LanguageIETF String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        productTicketContentService.deletePassbookImages(productId, language);
    }
}
