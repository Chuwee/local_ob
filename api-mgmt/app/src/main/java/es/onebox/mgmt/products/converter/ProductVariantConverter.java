package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariants;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductVariantsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariantPrices;
import es.onebox.mgmt.products.dto.ProductVariantDTO;
import es.onebox.mgmt.products.dto.ProductVariantsDTO;
import es.onebox.mgmt.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantDTO;
import es.onebox.mgmt.products.dto.UpdateProductVariantPricesDTO;
import es.onebox.mgmt.products.enums.ProductType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductVariantConverter {
    private ProductVariantConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductVariantsDTO fromMS(ProductVariants source, ProductType productType) {
        if (source == null) {
            return null;
        }
        ProductVariantsDTO result = new ProductVariantsDTO();
        result.setMetadata(source.getMetadata());
        result.setData(fromMS(source.getData(), productType));
        return result;
    }

    public static ProductVariantDTO fromMS(ProductVariant source, ProductType productType) {
        if (source == null) {
            return null;
        }
        ProductVariantDTO result = new ProductVariantDTO();
        result.setId(source.getId());
        result.setProduct(source.getProduct());
        result.setName(source.getName());
        result.setSku(source.getSku());
        result.setStock(source.getStock());
        result.setPrice(source.getPrice());
        result.setCreateDate(source.getCreateDate());
        result.setUpdateDate(source.getUpdateDate());

        if (productType.equals(ProductType.VARIANT)) {
            result.setVariantOption1(source.getVariantOption1());
            result.setVariantOption2(source.getVariantOption2());
            result.setVariantValue1(source.getVariantValue1());
            result.setVariantValue2(source.getVariantValue2());
            result.setProductVariantStatus(source.getProductVariantStatus());
        }
        return result;
    }

    public static UpdateProductVariant toEntity(UpdateProductVariantDTO productVariant) {
        if (productVariant == null) {
            return null;
        }

        UpdateProductVariant result = new UpdateProductVariant();
        result.setPrice(productVariant.getPrice());
        result.setSku(productVariant.getSku());
        result.setStock(productVariant.getStock());
        result.setStatus(productVariant.getProductVariantStatus());

        return result;
    }

    public static UpdateProductVariantPrices toEntity(UpdateProductVariantPricesDTO updateProductVariantPricesDTO) {
        if (updateProductVariantPricesDTO == null) {
            return null;
        }

        UpdateProductVariantPrices result = new UpdateProductVariantPrices();
        result.setPrice(updateProductVariantPricesDTO.getPrice());
        result.setVariants(updateProductVariantPricesDTO.getVariants());

        return result;
    }

    public static SearchProductVariantsFilter convertFilter(SearchProductVariantsFilterDTO searchProductVariantsFilterDTO) {
        SearchProductVariantsFilter searchProductVariantsFilter = new SearchProductVariantsFilter();
        searchProductVariantsFilter.setQ(searchProductVariantsFilterDTO.getQ());
        searchProductVariantsFilter.setStatus(searchProductVariantsFilterDTO.getStatus());
        searchProductVariantsFilter.setIds(searchProductVariantsFilterDTO.getIds());
        searchProductVariantsFilter.setStock(searchProductVariantsFilterDTO.getStock());
        searchProductVariantsFilter.setOffset(searchProductVariantsFilterDTO.getOffset());
        searchProductVariantsFilter.setLimit(searchProductVariantsFilterDTO.getLimit());

        return searchProductVariantsFilter;
    }

    private static List<ProductVariantDTO> fromMS(List<ProductVariant> source, ProductType productType) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(variant -> ProductVariantConverter.fromMS(variant, productType))
                .collect(Collectors.toList());
    }
}
