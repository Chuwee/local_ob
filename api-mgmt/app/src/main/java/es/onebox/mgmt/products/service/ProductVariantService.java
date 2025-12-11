package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariants;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductVariantsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariantPrices;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductVariantConverter;
import es.onebox.mgmt.products.dto.ProductVariantDTO;
import es.onebox.mgmt.products.dto.ProductVariantsDTO;
import es.onebox.mgmt.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantPricesDTO;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.products.enums.ProductType;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {
    private final ValidationService validationService;
    private final ProductsRepository productsRepository;

    public ProductVariantService(ValidationService validationService, ProductsRepository productsRepository) {
        this.validationService = validationService;
        this.productsRepository = productsRepository;
    }

    public ProductVariantsDTO searchProductVariants(Long productId, SearchProductVariantsFilterDTO filterDTO) {
        Product product = validationService.getAndCheckProduct(productId);

        if (product.getStockType().equals(ProductStockType.UNBOUNDED) && filterDTO.getStock() != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_VARIANT_FILTER_INVALID);
        }

        SearchProductVariantsFilter filter = ProductVariantConverter.convertFilter(filterDTO);
        ProductVariants variants = productsRepository.searchProductVariants(productId, filter);

        return ProductVariantConverter.fromMS(variants, product.getProductType());
    }

    public ProductVariantDTO getProductVariant(Long productId, Long variantId) {
        //TODO Check product attributes and attribute values
        Product product = validationService.getAndCheckProduct(productId);

        return ProductVariantConverter.fromMS(productsRepository.getProductVariant(productId, variantId),
                product.getProductType());
    }

    public void updateProductVariant(Long productId, Long variantId,
                                     UpdateProductVariantDTO updateProductVariantDTO) {
        //Check product exists
        Product product = validationService.getAndCheckProduct(productId);

        //Check product variant exists
        productsRepository.getProductVariant(productId, variantId);

        //Check price and stock are not being modified on active products
        checkProductVariantUpdatable(product, updateProductVariantDTO);

        UpdateProductVariant updateProductVariant = ProductVariantConverter.toEntity(updateProductVariantDTO);

        productsRepository.updateProductVariant(productId, variantId, updateProductVariant);
    }

    public void updateProductVariantPrices(Long productId,
                                           UpdateProductVariantPricesDTO updateProductVariantPricesDTO) {
        //Check product exists
        validationService.getAndCheckProduct(productId);

        UpdateProductVariantPrices updateProductVariantPrices = ProductVariantConverter.toEntity(updateProductVariantPricesDTO);

        productsRepository.updateProductVariantPrices(productId, updateProductVariantPrices);
    }

    public List<IdNameDTO> createVariantProductVariants(Long productId) {
        //Check product exists
        Product product = validationService.getAndCheckProduct(productId);

        //Check product is variant 1 -> simple 2 -> variant
        if (product.getProductType().equals(ProductType.SIMPLE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_VARIANT_PRODUCT_TYPE_INVALID);
        }
        return productsRepository.createVariantProductVariants(productId);
    }

    private void checkProductVariantUpdatable(Product product,
                                              UpdateProductVariantDTO updateProductVariantDTO) {
        if (updateProductVariantDTO.getProductVariantStatus() != null
                && product.getProductType().equals(ProductType.SIMPLE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_VARIANT_STATUS_NOT_UPDATABLE);
        }

        Long requestedStock = updateProductVariantDTO.getStock();
        Double requestedPrice = updateProductVariantDTO.getPrice();

        if ((requestedStock != null || requestedPrice != null) &&
                product.getProductState().equals(ProductState.ACTIVE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE);
        }
    }
}
