package es.onebox.mgmt.products.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.products.surcharges.dto.ProductSurchargeDTO;
import es.onebox.mgmt.products.surcharges.dto.ProductSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSurcharge;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductSurchargeService {

    private final ProductsRepository productsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public ProductSurchargeService(ProductsRepository productsRepository,
                                   SecurityManager securityManager,
                                   MasterdataService masterdataService,
                                   EntitiesRepository entitiesRepository) {
        this.productsRepository = productsRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public void setSurcharge(Long productId, ProductSurchargeListDTO productSurchargeListDTO) {
        Product product = checkProductPermissions(productId);

        validateSurcharges(productSurchargeListDTO);

        Set<String> requestCurrencies = productSurchargeListDTO.stream()
                .map(ProductSurchargeDTO::getRanges)
                .flatMap(Collection::stream)
                .map(RangeDTO::getCurrency)
                .collect(Collectors.toSet());

        if (requestCurrencies.size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        List<Currency> currencies = masterdataService.getCurrencies();
        Currency productCurrency = product.getCurrencyId() != null
                ? CurrenciesUtils.getCurrencyByCurrencyId(product.getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(product.getEntity().getId()));

        if (requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(productCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<ProductSurcharge> requests = productSurchargeListDTO
                .stream()
                .map(productSurchargeDTO -> ProductSurchargeConverter.toProductSurcharge(productSurchargeDTO, currencies, productCurrency))
                .collect(Collectors.toList());

        productsRepository.setSurcharge(productId, requests);
    }

    public List<ProductSurchargeDTO> getSurcharges(Long productId, List<SurchargeTypeDTO> types) {
        Product product = checkProductPermissions(productId);

        List<ProductSurcharge> productSurcharges = productsRepository.getSurcharges(productId, types);

        List<Currency> currencies = masterdataService.getCurrencies();

        Currency productCurrency = product.getCurrencyId() != null
                ? CurrenciesUtils.getCurrencyByCurrencyId(product.getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(product.getEntity().getId()));

        return ProductSurchargeConverter.toProductSurchargeDTO(productSurcharges, currencies, productCurrency);
    }

    private Product checkProductPermissions(Long productId) {
        Product product = productsRepository.getProduct(productId);

        if (product == null || ProductState.DELETED.equals(product.getProductState())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());
        return product;
    }

    private void validateSurcharges(List<ProductSurchargeDTO> productSurchargeDTOS) {

        if (productSurchargeDTOS.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.TYPE_MANDATORY);
        }

        if (productSurchargeDTOS.stream().anyMatch(this::hasFixedAndPercentageNull)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (hasTypesDuplicated(productSurchargeDTOS)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }
    }

    private static boolean hasTypesDuplicated(List<ProductSurchargeDTO> productSurchargeDTOS) {
        return productSurchargeDTOS.stream()
                .anyMatch(productSurchargeDTO ->
                        productSurchargeDTOS.stream()
                                .filter(singleSurcharge -> singleSurcharge.getType().equals(productSurchargeDTO.getType()))
                                .count() > 1);
    }

    private boolean hasFixedAndPercentageNull(ProductSurchargeDTO productSurchargeDTO) {
        if (CollectionUtils.isNotEmpty(productSurchargeDTO.getRanges())) {
            return productSurchargeDTO.getRanges().stream()
                    .anyMatch(rangeDTO -> rangeDTO.getValues().getFixed() == null && rangeDTO.getValues().getPercentage() == null);
        }
        throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_RANGE_MANDATORY);
    }
}
