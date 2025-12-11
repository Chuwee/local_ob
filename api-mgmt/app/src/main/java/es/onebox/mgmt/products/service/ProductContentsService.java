package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.ProductLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductValueLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductContentsConverter;
import es.onebox.mgmt.products.dto.ProductLiteralsDTO;
import es.onebox.mgmt.products.dto.ProductValueLiteralsDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductContentsService {

    private final ProductsRepository productsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ProductContentsService(ProductsRepository productsRepository, SecurityManager securityManager) {
        this.productsRepository = productsRepository;
        this.securityManager = securityManager;
    }

    public ProductLiteralsDTO getProductAttributeLiterals(final Long productId, final Long attributeId, final String languageCode) {
        validateRequest(productId, attributeId, null);
        ProductLiterals result = productsRepository.getProductAttributeLiterals(productId, attributeId, ConverterUtils.toLocale(languageCode));
        return ProductContentsConverter.toDTO(result);
    }

    public void upsertProductAttributeLiterals(final Long productId, final Long attributeId, final ProductLiteralsDTO body) {
        validateRequest(productId, attributeId, null);
        ProductLiterals out = ProductContentsConverter.toEntity(body);
        productsRepository.createOrUpdateProductAttributeLiterals(productId, attributeId, out);
    }

    public ProductLiteralsDTO getProductValueLiterals(final Long productId, final Long attributeId, final Long valueId, final String languageCode) {
        validateRequest(productId, attributeId, valueId);
        ProductLiterals result = productsRepository.getProductValueLiterals(productId, attributeId, valueId, ConverterUtils.toLocale(languageCode));
        return ProductContentsConverter.toDTO(result);
    }

    public ProductValueLiteralsDTO getProductBulkValueLiterals(final Long productId, final Long attributeId, final String languageCode) {
        validateRequest(productId, attributeId, null);
        ProductValueLiterals result = productsRepository.getProductBulkValueLiterals(productId, attributeId, ConverterUtils.toLocale(languageCode));
        return ProductContentsConverter.toBulkDTO(result);
    }

    public void upsertProductValueLiterals(final Long productId, final Long attributeId, final Long valueId, final ProductLiteralsDTO body) {
        validateRequest(productId, attributeId, valueId);
        ProductLiterals out = ProductContentsConverter.toEntity(body);
        productsRepository.createOrUpdateProductValueLiterals(productId, attributeId, valueId, out);
    }

    public void upsertBulkProductValueLiterals(final Long productId, final Long attributeId, final ProductValueLiteralsDTO body) {
        validateRequest(productId, attributeId, null);
        ProductValueLiterals out = ProductContentsConverter.toBulkEntity(body);
        productsRepository.createOrUpdateProductBulkValueLiterals(productId, attributeId, out);
    }

    private void validateRequest(final Long productId, final Long attributeId, final Long valueId) {
        Product product = productsRepository.getProduct(productId);
        if (product == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        ProductAttribute productAttribute = productsRepository.getProductAttribute(productId, attributeId);
        if(productAttribute == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
        }
        if(valueId != null) {
            ProductAttributeValue productAttributeValue = productsRepository.getProductAttributeValue(productId, attributeId, valueId);
            if(productAttributeValue == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_ATTRIBUTE_VALUE_NOT_FOUND);
            }
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());
    }
}
