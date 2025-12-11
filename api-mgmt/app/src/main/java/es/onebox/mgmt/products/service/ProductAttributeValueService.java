package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValues;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductAttributeValueConverter;
import es.onebox.mgmt.products.dto.CreateProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValuesDTO;
import es.onebox.mgmt.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeValueDTO;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.products.enums.ProductType;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductAttributeValueService {
    private final ValidationService validationService;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductAttributeValueService(ValidationService validationService, ProductsRepository productsRepository) {
        this.validationService = validationService;
        this.productsRepository = productsRepository;
    }

    public Long createProductAttributeValue(Long productId, Long attributeId,
                                            CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        Product product = validationService.getAndCheckProductAttribute(productId, attributeId, null);

        if (!product.getProductState().equals(ProductState.INACTIVE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_ATTRIBUTE_VALUE_NOT_INACTIVE_PRODUCT);
        }

        if (product.getProductType().equals(ProductType.SIMPLE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_PRODUCT_TYPE_FOR_VALUES);
        }

        return productsRepository.createProductAttributeValue(productId, attributeId,
                ProductAttributeValueConverter.convert(createProductAttributeValueDTO));
    }

    public ProductAttributeValueDTO getProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        validationService.getAndCheckProductAttribute(productId, attributeId, valueId);

        ProductAttributeValue productAttributeValue = productsRepository.getProductAttributeValue(productId, attributeId, valueId);

        return ProductAttributeValueConverter.toDto(productAttributeValue);
    }

    public ProductAttributeValuesDTO getProductAttributeValues(Long productId, Long attributeId, SearchProductAttributeValueFilterDTO searchProductAttributeValueFilterDTO) {
        validationService.getAndCheckProductAttribute(productId, attributeId, null);

        ProductAttributeValues productAttributeValues = productsRepository.getProductAttributeValues(productId, attributeId,
                searchProductAttributeValueFilterDTO);

        return ProductAttributeValueConverter.toDtos(productAttributeValues);
    }

    public void updateProductAttributeValue(Long productId, Long attributeId, Long valueId,
                                            UpdateProductAttributeValueDTO updateProductAttributeValueDTO) {
        validationService.getAndCheckProductAttribute(productId, attributeId, valueId);

        if(updateProductAttributeValueDTO.getPosition() != null && updateProductAttributeValueDTO.getPosition() < 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.POSITION_MUST_BE_POSITIVE);
        }

        ProductAttributeValue updateProductAttributeValue = ProductAttributeValueConverter.toEntity(updateProductAttributeValueDTO);

        productsRepository.updateProductAttributeValue(productId, attributeId, valueId, updateProductAttributeValue);
    }

    public void deleteProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        validationService.getAndCheckProductAttribute(productId, attributeId, valueId);

        productsRepository.deleteProductAttributeValue(productId, attributeId, valueId);
    }
}
