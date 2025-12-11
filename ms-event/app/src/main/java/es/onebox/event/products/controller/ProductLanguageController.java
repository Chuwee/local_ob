package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductLanguagesDTO;
import es.onebox.event.products.dto.UpdateProductLanguagesDTO;
import es.onebox.event.products.service.ProductLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;


@RestController
@RequestMapping(value = ProductLanguageController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductLanguageController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/languages";

    private final ProductLanguageService productLanguageService;

    @Autowired
    public ProductLanguageController(ProductLanguageService productLanguageService) {
        this.productLanguageService = productLanguageService;
    }


    @GetMapping()
    public ResponseEntity<ProductLanguagesDTO> getProductLanguages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductLanguagesDTO productLanguages = productLanguageService.getProductLanguages(productId);
        return new ResponseEntity<>(productLanguages, HttpStatus.OK);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductLanguagesDTO> updateProductLanguages(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                      @Valid @RequestBody UpdateProductLanguagesDTO updateProductLanguagesDTO) {
        ProductLanguagesDTO productLanguages = productLanguageService.updateProductLanguages(productId, updateProductLanguagesDTO);
        return new ResponseEntity<>(productLanguages, HttpStatus.OK);
    }

}
