package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValuesDTO;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.event.products.dto.UpdateProductAttributeValueDTO;
import es.onebox.event.products.service.ProductAttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = ProductAttributeValueController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductAttributeValueController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/attributes/{attributeId}/values";

    private final ProductAttributeValueService productAttributeValueService;

    @Autowired
    public ProductAttributeValueController(ProductAttributeValueService productAttributeValueService) {
        this.productAttributeValueService = productAttributeValueService;
    }

    @GetMapping()
    public ResponseEntity<ProductAttributeValuesDTO> getProductAttributeValues(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                               @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                                                               @Valid SearchProductAttributeValueFilterDTO filter) {
        ProductAttributeValuesDTO productAttributeValuesDTO = productAttributeValueService.getProductAttributeValues(productId,
                attributeId, filter);

        return new ResponseEntity<>(productAttributeValuesDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDTO> createProductAttributeValue
            (@PathVariable Long productId, @PathVariable Long attributeId,
             @RequestBody CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        Long productAttributeValueId =
                productAttributeValueService.createProductAttributeValue(productId, attributeId, createProductAttributeValueDTO);

        return new ResponseEntity<>(new IdDTO(productAttributeValueId), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{valueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductAttribute(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                       @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                       @Min(value = 1, message = "valueId must be above 0") @PathVariable Long valueId,
                                       @Valid @RequestBody UpdateProductAttributeValueDTO updateProductAttributeValueDTO) {
        productAttributeValueService.updateProductAttributeValue(productId, attributeId, valueId, updateProductAttributeValueDTO);
    }

    @GetMapping("/{valueId}")
    public ProductAttributeValueDTO getProductAttributeValue
            (@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
             @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
             @Min(value = 1, message = "valueId must be above 0") @PathVariable Long valueId) {
        return productAttributeValueService.getProductAttributeValue(productId, attributeId, valueId);
    }

    @DeleteMapping(value = "/{valueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductAttribute(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                       @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                                       @Min(value = 1, message = "valueId must be above 0") @PathVariable Long valueId) {
        productAttributeValueService.deleteProductAttributeValue(productId, attributeId, valueId);
    }
}
