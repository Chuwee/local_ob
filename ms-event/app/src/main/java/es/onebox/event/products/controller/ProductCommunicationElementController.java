package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementsTextsDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.event.products.service.ProductCommunicationElementService;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;


@RestController
@RequestMapping(value = ProductCommunicationElementController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductCommunicationElementController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/communication-elements";

    private final ProductCommunicationElementService productCommunicationElementService;

    @Autowired
    public ProductCommunicationElementController(ProductCommunicationElementService productCommunicationElementService) {
        this.productCommunicationElementService = productCommunicationElementService;
    }

    @PostMapping("/texts")
    @ResponseStatus(HttpStatus.OK)
    public ProductCommunicationElementsTextsDTO createProductCommunicationElementsTexts(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                        @Valid @RequestBody CreateProductCommunicationElementsTextsDTO createProductCommunicationElementsTextsDTO) {
        return productCommunicationElementService.createProductCommunicationElementsTexts(productId, createProductCommunicationElementsTextsDTO);
    }

    @GetMapping("/texts")
    public ResponseEntity<ProductCommunicationElementsTextsDTO> getProductCommunicationElementsTexts(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductCommunicationElementsTextsDTO productCommunicationElementsTextsDTO = productCommunicationElementService.getProductCommunicationElementsTexts(productId);
        return new ResponseEntity<>(productCommunicationElementsTextsDTO, HttpStatus.OK);
    }


    @PostMapping("/images")
    @ResponseStatus(HttpStatus.OK)
    public void createProductCommunicationElementsImages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                         @Valid @RequestBody CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO) {
        productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImagesDTO);
    }

    @GetMapping("/images")
    public ResponseEntity<ProductCommunicationElementsImagesDTO> getProductCommunicationElementsImages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductCommunicationElementsImagesDTO productCommunicationElementsImagesDTO = productCommunicationElementService.getProductCommunicationElementsImages(productId);
        return new ResponseEntity<>(productCommunicationElementsImagesDTO, HttpStatus.OK);
    }

    @DeleteMapping("/images/languages/{language}/types/{type}/positions/{position}")
    public void deleteProductCommunicationElementsImages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId, @PathVariable String language,
                                                         @PathVariable ProductCommunicationElementsImagesType type,
                                                         @Min(value = 0, message = "position must be above 0") @PathVariable Long position) {
        productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, type, position);
    }


}
