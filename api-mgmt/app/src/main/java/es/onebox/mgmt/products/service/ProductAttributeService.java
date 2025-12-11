package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributes;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductAttributeConverter;
import es.onebox.mgmt.products.dto.CreateProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributesDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductAttributeService {
    private final ValidationService validationService;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductAttributeService(ValidationService validationService, ProductsRepository productsRepository) {
        this.validationService = validationService;
        this.productsRepository = productsRepository;
    }

    public Long createProductAttribute(Long productId, CreateProductAttributeDTO createProductAttributeDTO) {
        validationService.getAndCheckProductAttribute(productId, null, null);

        return productsRepository.createProductAttribute(productId,
                ProductAttributeConverter.convert(createProductAttributeDTO));
    }

    public ProductAttributeDTO getProductAttribute(Long productId, Long attributeId) {
        validationService.getAndCheckProductAttribute(productId, attributeId, null);

        ProductAttribute productAttribute = productsRepository.getProductAttribute(productId, attributeId);

        return ProductAttributeConverter.toDto(productAttribute);
    }

    public ProductAttributesDTO getProductAttributes(Long productId) {
        validationService.getAndCheckProductAttribute(productId, null, null);

        ProductAttributes productAttributes = productsRepository.getProductAttributes(productId);

        return ProductAttributeConverter.toDto(productAttributes);
    }

    public void updateProductAttribute(Long productId, Long attributeId, UpdateProductAttributeDTO updateProductAttributeDTO) {
        validationService.getAndCheckProductAttribute(productId, attributeId, null);

        if(updateProductAttributeDTO.getPosition() != null && updateProductAttributeDTO.getPosition() < 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.POSITION_MUST_BE_POSITIVE);
        }
        ProductAttribute updateProductAttribute = ProductAttributeConverter.toEntity(updateProductAttributeDTO);

        productsRepository.updateProductAttribute(productId, attributeId, updateProductAttribute);
    }

    public void deleteProductAttribute(Long productId, Long attributeId) {
        validationService.getAndCheckProductAttribute(productId, attributeId, null);

        productsRepository.deleteProductAttribute(productId, attributeId);
    }

}
