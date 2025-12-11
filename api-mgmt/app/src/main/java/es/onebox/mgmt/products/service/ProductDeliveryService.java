package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDelivery;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductDeliveryConverter;
import es.onebox.mgmt.products.dto.ProductDeliveryDTO;
import es.onebox.mgmt.products.dto.UpdateProductDeliveryDTO;
import es.onebox.mgmt.products.enums.ProductDeliveryType;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ProductDeliveryService {
    private final SecurityManager securityManager;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductDeliveryService(SecurityManager securityManager, ProductsRepository productsRepository) {
        this.securityManager = securityManager;
        this.productsRepository = productsRepository;
    }

    public ProductDeliveryDTO getProductDelivery(Long productId) {
        Product product = productsRepository.getProduct(productId);
        if (product == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());
        ProductDelivery productDelivery = productsRepository.getProductDelivery(productId);
        return ProductDeliveryConverter.toDto(productDelivery);
    }

    public ProductDeliveryDTO updateProductDelivery(Long productId, UpdateProductDeliveryDTO updateProductDeliveryDTO) {
        Product product = productsRepository.getProduct(productId);
        if (product == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }

        if(ProductStockType.SESSION_BOUNDED.equals(product.getStockType()) && updateProductDeliveryDTO.getDeliveryType() != null && !ProductDeliveryType.SESSION.equals(updateProductDeliveryDTO.getDeliveryType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_SESSION_BOUNDED_DELIVERY_TYPE);
        }

        securityManager.checkEntityAccessible(product.getEntity().getId());

        if (product.getProductState().equals(ProductState.ACTIVE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_DELIVERY_NOT_UPDATABLE);
        }

        if (updateProductDeliveryDTO.getDeliveryType().equals(ProductDeliveryType.FIXED_DATES)) {
            checkDeliveryDates(updateProductDeliveryDTO);
        }

        ProductDelivery productDelivery = ProductDeliveryConverter.convert(updateProductDeliveryDTO);
        ProductDelivery result = productsRepository.updateProductDelivery(productId, productDelivery);

        return ProductDeliveryConverter.toDto(result);
    }

    private void checkDeliveryDates(UpdateProductDeliveryDTO updateProductDeliveryDTO) {
        if (updateProductDeliveryDTO.getDeliveryDateFrom() == null || updateProductDeliveryDTO.getDeliveryDateTo() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.DELIVERY_DATES_REQUIRED);
        }

        if (updateProductDeliveryDTO.getDeliveryDateFrom().isAfter(updateProductDeliveryDTO.getDeliveryDateTo())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_DELIVERY_DATES);
        }
    }
}
