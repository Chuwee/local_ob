package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributesDTO;
import es.onebox.event.products.dto.UpdateProductAttributeDTO;
import es.onebox.event.products.service.ProductAttributeService;
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
@RequestMapping(value = ProductAttributeController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductAttributeController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/attributes";

    private final ProductAttributeService productAttributeService;

    @Autowired
    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @GetMapping()
    public ResponseEntity<ProductAttributesDTO> getProductAttributes(@PathVariable Long productId) {
        ProductAttributesDTO productAttributesDTO = productAttributeService.getProductAttributes(productId);

        return new ResponseEntity<>(productAttributesDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDTO> createProductAttribute(@PathVariable Long productId,
                                                        @RequestBody CreateProductAttributeDTO createProductAttributeDTO) {
        Long productAttributeId = productAttributeService.createProductAttribute(productId, createProductAttributeDTO);

        return new ResponseEntity<>(new IdDTO(productAttributeId), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{attributeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductAttribute(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                              @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId,
                              @Valid @RequestBody UpdateProductAttributeDTO updateProductAttributeDTO) {
        productAttributeService.updateProductAttribute(productId, attributeId, updateProductAttributeDTO);
    }

    @GetMapping("/{attributeId}")
    public ProductAttributeDTO getProductAttribute(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                   @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId) {
        return productAttributeService.getProductAttribute(productId, attributeId);
    }

    @DeleteMapping(value = "/{attributeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductAttribute(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                       @Min(value = 1, message = "attributeId must be above 0") @PathVariable Long attributeId) {
        productAttributeService.deleteProductAttribute(productId, attributeId);
    }
}
