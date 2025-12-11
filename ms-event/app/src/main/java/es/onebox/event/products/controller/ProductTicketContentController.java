package es.onebox.event.products.controller;

import es.onebox.event.common.enums.TicketType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductTicketContentListImageDTO;
import es.onebox.event.products.dto.ProductTicketContentListTextDTO;
import es.onebox.event.products.enums.TicketContentImageType;
import es.onebox.event.products.service.ProductTicketContentService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ProductTicketContentController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductTicketContentController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/ticket-contents/";

    private final ProductTicketContentService productTicketContentService;

    @Autowired
    public ProductTicketContentController(ProductTicketContentService productTicketContentService) {
        this.productTicketContentService = productTicketContentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/PDF/texts")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductTicketContentsPdfTexts(@PathVariable Long productId,
                                                            @RequestBody ProductTicketContentListTextDTO productTicketContentListTextDTO) {
        productTicketContentService.createOrUpdateText(productId, TicketType.PDF, productTicketContentListTextDTO);
    }

    @GetMapping("PDF/texts")
    public ResponseEntity<ProductTicketContentListTextDTO> getProductTicketContentsPdfTexts(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductTicketContentListTextDTO productTicketContentListTextDTO = productTicketContentService.getTexts(productId, TicketType.PDF);
        return new ResponseEntity<>(productTicketContentListTextDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/PASSBOOK/texts")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductTicketContentsPassbookTexts(@PathVariable Long productId,
                                                                 @RequestBody ProductTicketContentListTextDTO productTicketContentListTextDTO) {
        productTicketContentService.createOrUpdateText(productId, TicketType.PASSBOOK, productTicketContentListTextDTO);
    }

    @GetMapping("PASSBOOK/texts")
    public ResponseEntity<ProductTicketContentListTextDTO> getProductTicketContentsPassbookTexts(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductTicketContentListTextDTO productTicketContentListTextDTO = productTicketContentService.getTexts(productId, TicketType.PASSBOOK);
        return new ResponseEntity<>(productTicketContentListTextDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/PDF/images")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductTicketContentsPdfImage(@PathVariable Long productId,
                                                            @RequestBody ProductTicketContentListImageDTO productTicketContentListImageDTO) {
        productTicketContentService.createOrUpdateImage(productId, TicketType.PDF, productTicketContentListImageDTO);
    }

    @GetMapping("PDF/images")
    public ResponseEntity<ProductTicketContentListImageDTO> getProductTicketContentsPdfImages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductTicketContentListImageDTO productTicketContentListImageDTO = productTicketContentService.getImages(productId, TicketType.PDF);
        return new ResponseEntity<>(productTicketContentListImageDTO, HttpStatus.OK);
    }

    @DeleteMapping("PDF/images/languages/{language}")
    public void deleteProductTicketContentPdfImage(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId, @PathVariable String language) {
        productTicketContentService.deleteImage(productId, language, TicketType.PDF, TicketContentImageType.BODY);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/PASSBOOK/images")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductTicketContentsPassbookImage(@PathVariable Long productId,
                                                                 @RequestBody ProductTicketContentListImageDTO productTicketContentListImageDTO) {
        productTicketContentService.createOrUpdateImage(productId, TicketType.PASSBOOK, productTicketContentListImageDTO);
    }

    @GetMapping("PASSBOOK/images")
    public ResponseEntity<ProductTicketContentListImageDTO> getProductTicketContentsPassbookImages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductTicketContentListImageDTO productTicketContentListImageDTO = productTicketContentService.getImages(productId, TicketType.PASSBOOK);
        return new ResponseEntity<>(productTicketContentListImageDTO, HttpStatus.OK);
    }

    @DeleteMapping("PASSBOOK/images/languages/{language}")
    public void deleteProductTicketContentPassbookImage(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId, @PathVariable String language) {
        productTicketContentService.deleteImage(productId, language, TicketType.PASSBOOK, TicketContentImageType.THUMBNAIL);
    }
}
