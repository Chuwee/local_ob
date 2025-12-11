package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.Products;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguages;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductConverter;
import es.onebox.mgmt.products.dto.CreateProductDTO;
import es.onebox.mgmt.products.dto.ProductDTO;
import es.onebox.mgmt.products.dto.ProductsDTO;
import es.onebox.mgmt.products.dto.SearchProductFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductDTO;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final SecurityManager securityManager;
    private final MasterdataRepository masterdataRepository;
    private final ProductsRepository productsRepository;
    private final ValidationService validationService;
    private final EntitiesRepository entitiesRepository;
    private final OrdersRepository ordersRepository;


    @Autowired
    public ProductService(SecurityManager securityManager, ProductsRepository productsRepository,
                          ValidationService validationService, EntitiesRepository entitiesRepository,
                          MasterdataRepository masterdataRepository, OrdersRepository ordersRepository) {
        this.securityManager = securityManager;
        this.productsRepository = productsRepository;
        this.validationService = validationService;
        this.entitiesRepository = entitiesRepository;
        this.masterdataRepository = masterdataRepository;
        this.ordersRepository = ordersRepository;
    }

    public Long createProduct(CreateProductDTO product) {
        securityManager.checkEntityAccessible(product.getEntityId());

        Long currencyId = getCurrencyId(product.getEntityId(), product.getCurrencyCode());

        // Create product
        Long productId = productsRepository.createProduct(ProductConverter.toEntity(product, currencyId));

        // Create default product language
        Entity entity = entitiesRepository.getEntity(product.getEntityId());

        UpdateProductLanguages updateProductLanguages = new UpdateProductLanguages();
        UpdateProductLanguage updateProductLanguage = new UpdateProductLanguage();
        updateProductLanguage.setLanguageId(entity.getLanguage().getId());
        updateProductLanguage.setCode(entity.getLanguage().getCode());
        updateProductLanguage.setIsDefault(true);
        updateProductLanguages.add(updateProductLanguage);
        productsRepository.updateProductLanguages(productId, updateProductLanguages);

        return productId;
    }

    public ProductDTO getProduct(Long productId) {
        Product product = validationService.getAndCheckProduct(productId);

        return ProductConverter.toDto(product, masterdataRepository.getCurrencies(), ordersRepository.productHasOrders(productId));
    }

    public void deleteProduct(Long productId) {
        validationService.getAndCheckProduct(productId);

        productsRepository.deleteProduct(productId);
    }

    public void updateProduct(Long productId, UpdateProductDTO updateProductDTO) {
        Product productToUpdate = validationService.getAndCheckProduct(productId);

        if (updateProductDTO.getCurrencyCode() != null && !isMultiCurrencyEntity(productToUpdate.getEntity().getId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        UpdateProduct updateProduct = ProductConverter.toEntity(updateProductDTO);
        fillCurrency(updateProduct, updateProductDTO, productToUpdate.getEntity().getId());

        productsRepository.updateProduct(productId, updateProduct);
    }

    public ProductsDTO searchProducts(SearchProductFilterDTO searchProductFilter) {
        securityManager.checkEntityAccessible(searchProductFilter);
        List<Long> entityIds = new ArrayList<>();
        if (!SecurityUtils.isOperatorEntity()) {
            if (searchProductFilter.getEntityId() != null) {
                securityManager.checkEntityAccessible(searchProductFilter.getEntityId());
                entityIds.add(searchProductFilter.getEntityId());
            } else {
                entityIds.add(SecurityUtils.getUserEntityId());
            }
        } else {
            entityIds.add(searchProductFilter.getEntityId());
        }
        if (searchProductFilter.getProductState() != null &&
                searchProductFilter.getProductState().contains(ProductState.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_STATE_NOT_VALID);
        }
        SearchProductFilter filter = ProductConverter.convertFilter(searchProductFilter, entityIds,
                masterdataRepository.getCurrencies());
        Products products = productsRepository.searchProducts(filter);

        return ProductConverter.toDtoList(products, masterdataRepository.getCurrencies());
    }

    private void fillCurrency(UpdateProduct product, UpdateProductDTO productDTO, Long productEntityId) {
        //Check if currency exists, otherwise throw an exception
        if (productDTO.getCurrencyCode() != null) {
            product.setCurrencyId(entitiesRepository.getCachedOperator(productEntityId).getCurrencies()
                    .getSelected()
                    .stream()
                    .filter(currency -> currency.getCode().equals(productDTO.getCurrencyCode()))
                    .findFirst()
                    .map(Currency::getId)
                    .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.EVENT_CURRENCY_NOT_MATCH_OPERATOR)));
        }
    }

    private boolean isMultiCurrencyEntity(Long entityId) {
        Operator entityOperator = entitiesRepository.getCachedOperator(entityId);

        return BooleanUtils.isTrue(entityOperator.getUseMultiCurrency());
    }

    private Long getCurrencyId(Long entityId, String currencyCode) {
        Operator entityOperator = entitiesRepository.getCachedOperator(entityId);

        Long currencyId = entityOperator.getCurrency().getId().longValue();

        if (BooleanUtils.isTrue(entityOperator.getUseMultiCurrency())) {
            if (currencyCode != null) {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(currencyCode))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.EVENT_CURRENCY_NOT_MATCH_OPERATOR));
            } else {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(entityOperator.getCurrencies().getDefaultCurrency()))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.ERROR_OPERATOR_WITHOUT_MULTICURRENCY_DEFAULT));
            }
        }

        return currencyId;
    }
}
