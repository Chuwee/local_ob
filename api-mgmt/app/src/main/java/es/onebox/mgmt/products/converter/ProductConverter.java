package es.onebox.mgmt.products.converter;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.categories.CategoryDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.event.ProductEventsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.TaxMode;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.Products;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductType;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.products.dto.CreateProductDTO;
import es.onebox.mgmt.products.dto.ProductDTO;
import es.onebox.mgmt.products.dto.ProductEventsFilterDTO;
import es.onebox.mgmt.products.dto.ProductSettingDTO;
import es.onebox.mgmt.products.dto.ProductUISettingsDTO;
import es.onebox.mgmt.products.dto.ProductsDTO;
import es.onebox.mgmt.products.dto.SearchProductFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductDTO;
import es.onebox.mgmt.products.enums.TaxModeDTO;
import es.onebox.mgmt.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductConverter {
    private ProductConverter() {
    }

    public static ProductDTO toDto(Product product, List<Currency> availableCurrencies, Boolean hasSales) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setProductState(product.getProductState());
        productDTO.setEntity(product.getEntity());
        productDTO.setProducer(product.getProducer());
        productDTO.setCreateDate(product.getCreateDate());
        productDTO.setUpdateDate(product.getUpdateDate());
        productDTO.setProductType(product.getProductType());
        productDTO.setStockType(product.getStockType());
        productDTO.setTax(product.getTax());
        productDTO.setSurchargeTax(product.getSurchargeTax());
        if (product.getCurrencyId() != null) {
            productDTO.setCurrencyCode(CurrenciesUtils.getCurrencyCode(availableCurrencies, product.getCurrencyId()));
        }
        productDTO.setTicketTemplateId(product.getTicketTemplateId());
        productDTO.setProductUiSettings(new ProductUISettingsDTO());
        productDTO.getProductUiSettings().setHideDeliveryPoint(product.getHideDeliveryPoint());
        productDTO.getProductUiSettings().setHideDeliveryDateTime(product.getHideDeliveryDateTime());
        productDTO.setHasSales(hasSales);

        ProductSettingDTO settings = null;
        if (product.getCategory() != null || product.getCustomCategory() != null || product.getTaxMode() != null) {
            settings = new ProductSettingDTO();
            settings.setCategories(new CategoriesDTO());
            if (product.getCategory() != null) {
                settings.getCategories().setBase(new CategoryDTO());
                settings.getCategories().getBase().setId(product.getCategory().getId());
                settings.getCategories().getBase().setDescription(product.getCategory().getDescription());
                settings.getCategories().getBase().setCode(product.getCategory().getCode());
            }
            if (product.getCustomCategory() != null) {
                settings.getCategories().setCustom(new CategoryDTO());
                settings.getCategories().getCustom().setId(product.getCustomCategory().getId());
                settings.getCategories().getCustom().setDescription(product.getCustomCategory().getDescription());
                settings.getCategories().getCustom().setCode(product.getCustomCategory().getCode());
            }
            settings.setTaxMode(TaxModeDTO.fromMs(product.getTaxMode()));
        }
        productDTO.setSettings(settings);

        return productDTO;
    }

    public static ProductsDTO toDtoList(Products products, List<Currency> availableCurrencies) {
        ProductsDTO productsDTO = new ProductsDTO();
        List<ProductDTO> productsDTOList = new ArrayList<>();
        if (products.getData() != null) {
            products.getData().forEach(p -> productsDTOList.add(toDto(p, availableCurrencies, null)));
        }
        productsDTO.setData(productsDTOList);
        productsDTO.setMetadata(products.getMetadata());
        return productsDTO;
    }

    public static UpdateProduct toEntity(UpdateProductDTO updateProductDTO) {
        if (updateProductDTO == null) {
            return null;
        }

        UpdateProduct updateProduct = new UpdateProduct();

        updateProduct.setProductState(updateProductDTO.getProductState());
        updateProduct.setTaxId(updateProductDTO.getTaxId());
        updateProduct.setSurchargeTaxId(updateProductDTO.getSurchargeTaxId());
        updateProduct.setName(updateProductDTO.getName());
        if (updateProductDTO.getProductUiSettings() != null) {
            updateProduct.setHideDeliveryDateTime(updateProductDTO.getProductUiSettings().getHideDeliveryDateTime());
            updateProduct.setHideDeliveryPoint(updateProductDTO.getProductUiSettings().getHideDeliveryPoint());
        }
        ProductSettingDTO settings = updateProductDTO.getSettings();
        if (settings != null) {
            if (settings.getCategories() != null) {
                if (settings.getCategories().getBase() != null) {
                    updateProduct.setCategory(new IdDTO(settings.getCategories().getBase().getId()));
                }
                if (settings.getCategories().getCustom() != null) {
                    updateProduct.setCustomCategory(new IdDTO(settings.getCategories().getCustom().getId()));
                }
            }
            updateProduct.setTaxMode(TaxMode.fromDTO(updateProductDTO.getSettings().getTaxMode()));
        }


        return updateProduct;
    }

    public static CreateProduct toEntity(CreateProductDTO source, Long currencyId) {
        if (source == null) {
            return null;
        }
        return new CreateProduct(source.getEntityId(), source.getProducerId(), source.getName(),
                ProductStockType.valueOf(source.getStockType().name()),
                ProductType.valueOf(source.getProductType().name()), currencyId);
    }

    public static SearchProductFilter convertFilter(SearchProductFilterDTO searchProductFilterDTO, List<Long> entityIds,
                                                    List<Currency> availableCurrencies) {
        SearchProductFilter searchProductFilter = new SearchProductFilter();
        searchProductFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        searchProductFilter.setProductState(searchProductFilterDTO.getProductState());
        searchProductFilter.setQ(searchProductFilterDTO.getQ());
        searchProductFilter.setStockType(searchProductFilterDTO.getStockType());
        searchProductFilter.setProductType(searchProductFilterDTO.getProductType());
        searchProductFilter.setEntityIds(entityIds);
        if (searchProductFilterDTO.getCurrencyCode() != null) {
            searchProductFilter.setCurrencyId(CurrenciesUtils.getCurrencyId(availableCurrencies, searchProductFilterDTO.getCurrencyCode()));
        }
        searchProductFilter.setEventIds(searchProductFilterDTO.getEventIds());
        searchProductFilter.setSessionIds(searchProductFilterDTO.getSessionIds());
        searchProductFilter.setEventSessionSelectionType(searchProductFilterDTO.getEventSessionSelectionType());
        searchProductFilter.setSort(searchProductFilterDTO.getSort());
        searchProductFilter.setOffset(searchProductFilterDTO.getOffset());
        searchProductFilter.setLimit(searchProductFilterDTO.getLimit());

        return searchProductFilter;
    }

    public static ProductEventsFilter convertFilter(ProductEventsFilterDTO filterDTO) {
        ProductEventsFilter productEventsFilter = new ProductEventsFilter();

        productEventsFilter.setEventStatus(filterDTO.getEventStatus());
        productEventsFilter.setStatus(filterDTO.getStatus());
        productEventsFilter.setStartDate(filterDTO.getStartDate());

        return productEventsFilter;
    }
}
