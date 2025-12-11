package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductVariantDTO;
import es.onebox.event.products.dto.ProductVariantsDTO;
import es.onebox.event.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.event.products.dto.UpdateProductVariantDTO;
import es.onebox.event.products.dto.UpdateProductVariantPricesDTO;
import es.onebox.event.products.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/products/{productId}/variants")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @Autowired
    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping()
    public ResponseEntity<ProductVariantsDTO> searchProductVariants
            (@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
             @Valid SearchProductVariantsFilterDTO searchProductVariantsFilterDTO) {
        ProductVariantsDTO productVariantsDTO =
                productVariantService.searchProductVariants(productId, searchProductVariantsFilterDTO);

        return new ResponseEntity<>(productVariantsDTO, HttpStatus.OK);
    }

    @GetMapping("/{variantId}")
    public ProductVariantDTO getProductVariant(@Min(value = 1, message = "productId must be above 0") @PathVariable(value = "productId") Long productId,
                                               @Min(value = 1, message = "variantId must be above 0") @PathVariable(value = "variantId") Long variantId) {
        return productVariantService.getProductVariant(productId, variantId);
    }

    @PutMapping(value = "/{variantId}")
    public void updateProductVariant(@Min(value = 1, message = "productId must be above 0") @PathVariable(value = "productId") Long productId,
                                     @Min(value = 1, message = "variantId must be above 0") @PathVariable(value = "variantId") Long variantId,
                                     @Valid @RequestBody UpdateProductVariantDTO updateProductVariantDTO) {

        productVariantService.updateProductVariant(productId, variantId, updateProductVariantDTO);
    }

    @PutMapping(value = "/prices")
    public void updateProductVariantPrices(@Min(value = 1, message = "productId must be above 0") @PathVariable(value = "productId") Long productId,
                                           @Valid @RequestBody UpdateProductVariantPricesDTO updateProductVariantPricesDTO) {

        productVariantService.updateProductVariantPrices(productId, updateProductVariantPricesDTO);
    }

    @PostMapping()
    public ResponseEntity<List<IdNameDTO>> createProductVariants(@Min(value = 1, message = "productId must be above 0") @PathVariable(value = "productId") Long productId) {
        List<IdNameDTO> productVariants = productVariantService.createVariantProductVariants(productId);

        return new ResponseEntity<>(productVariants, HttpStatus.CREATED);
    }
}
