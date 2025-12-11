package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateProductDTO;
import es.onebox.event.products.dto.ProductDTO;
import es.onebox.event.products.dto.ProductsDTO;
import es.onebox.event.products.dto.SearchProductFilterDTO;
import es.onebox.event.products.dto.UpdateProductDTO;
import es.onebox.event.products.service.ProductService;
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
@RequestMapping(ApiConfig.BASE_URL + "/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDTO> createProduct(@Valid @RequestBody CreateProductDTO product) {
        Long productId = productService.createProduct(product);

        return new ResponseEntity<>(new IdDTO(productId), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                              @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        productService.updateProduct(productId, updateProductDTO);
    }

    @GetMapping("/{productId}")
    public ProductDTO getProduct(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @DeleteMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        productService.deleteProduct(productId);
    }

    @GetMapping()
    public ResponseEntity<ProductsDTO> searchProducts(@Valid SearchProductFilterDTO searchProductFilterDTO) {
        ProductsDTO productsDTO = productService.searchProducts(searchProductFilterDTO);

        return new ResponseEntity<>(productsDTO, HttpStatus.OK);
    }
}
