package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductLiteralsDTO;
import es.onebox.event.products.dto.ProductValueLiteralsDTO;
import es.onebox.event.products.service.ProductContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = ProductContentsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductContentsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}";

    private final ProductContentService productContentService;

    @Autowired
    public ProductContentsController(ProductContentService productContentService) {
        this.productContentService = productContentService;
    }

    @GetMapping(value = "/attributes/{attributeId}/channel-contents/texts")
    public ResponseEntity<ProductLiteralsDTO> getProductAttributeLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                          @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                                          @RequestParam(required = false) String language) {
        ProductLiteralsDTO productLiteralsDTO = productContentService.getProductAttributeLiterals(productId, attributeId, language);

        return new ResponseEntity<>(productLiteralsDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/attributes/{attributeId}/channel-contents/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductAttributeLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                       @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                              @RequestBody ProductLiteralsDTO productLiteralsDTO) {
        productContentService.createOrUpdateProductAttributeLiterals(productId, attributeId, productLiteralsDTO);
    }

    @GetMapping(value = "/attributes/{attributeId}/values/{valueId}/channel-contents/texts")
    public ResponseEntity<ProductLiteralsDTO> getProductValueLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                      @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                                          @Min(value = 1, message = "valueId must be above 0") @PathVariable Long valueId,
                                                                          @RequestParam(required = false) String language) {
        ProductLiteralsDTO productLiteralsDTO = productContentService.getProductValueLiterals(productId, attributeId, valueId, language);

        return new ResponseEntity<>(productLiteralsDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/attributes/{attributeId}/values/channel-contents/texts")
    public ResponseEntity<ProductValueLiteralsDTO> getProductBulkValueLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                      @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                                      @RequestParam(required = false) String language) {
        ProductValueLiteralsDTO productValueLiteralDTOS = productContentService.getProductBulkValueLiterals(productId, attributeId, language);

        return new ResponseEntity<>(productValueLiteralDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/attributes/{attributeId}/values/{valueId}/channel-contents/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductValueLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                   @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                       @Min(value = 1, message = "valueId must be above 0") @PathVariable Long valueId,
                                                       @RequestBody ProductLiteralsDTO productLiteralsDTO) {
        productContentService.createOrUpdateProductValueLiterals(productId, attributeId, valueId, productLiteralsDTO);
    }

    @PostMapping(value = "/attributes/{attributeId}/values/channel-contents/texts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateProductBulkValueLiterals(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                   @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                   @RequestBody ProductValueLiteralsDTO productValueLiteralDTOS) {
        productContentService.createOrUpdateProductBulkValueLiterals(productId, attributeId, productValueLiteralDTOS);
    }

}
