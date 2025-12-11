package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.ProductTicketLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductContentsConverter;
import es.onebox.mgmt.products.dto.ProductTicketLiteralsDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductTicketContentsService {
    private final ProductsRepository productsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ProductTicketContentsService(ProductsRepository productsRepository, SecurityManager securityManager) {
        this.productsRepository = productsRepository;
        this.securityManager = securityManager;
    }

    public ProductTicketLiteralsDTO getProductTicketLiterals(final Long productId, final String languageCode, final String key) {
        validateRequest(productId, languageCode);
        ProductTicketLiterals result = productsRepository.getProductTicketLiterals(productId, convertLanguage(languageCode), key);
        return ProductContentsConverter.toDTO(result);
    }

    public void upsertProductTicketLiterals(final Long productId, final String languageCode, final ProductTicketLiteralsDTO body) {
        validateRequest(productId, languageCode);
        ProductTicketLiterals out = ProductContentsConverter.toDTO(body);
        productsRepository.createOrUpdateProductTicketLiterals(productId, convertLanguage(languageCode), out);
    }

    private void validateRequest(final Long productId, String languageCode) {
        Product product = productsRepository.getProduct(productId);
        if (product == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        ProductLanguages productLanguages = productsRepository.getProductLanguages(productId);
        if (productLanguages.stream().noneMatch(pl -> pl.getCode().equals(languageCode))) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_LANGUAGE_NOT_RELATED);
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());
    }

    private static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }
}
