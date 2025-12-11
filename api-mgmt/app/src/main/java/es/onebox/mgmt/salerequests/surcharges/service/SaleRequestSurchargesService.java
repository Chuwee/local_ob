package es.onebox.mgmt.salerequests.surcharges.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesExtendedDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.surcharges.respository.SaleRequestSurchargesRepository;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleRequestSurchargesService {

    private final SecurityManager securityManager;
    private final SaleRequestSurchargesRepository saleRequestSurchargesRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SaleRequestSurchargesService(SecurityManager securityManager,
                                        SaleRequestSurchargesRepository saleRequestSurchargesRepository,
                                        SaleRequestsRepository saleRequestsRepository, MasterdataService masterdataService,
                                        EntitiesRepository entitiesRepository) {
        this.securityManager = securityManager;
        this.saleRequestSurchargesRepository = saleRequestSurchargesRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<SaleRequestSurchargeDTO> saleRequestSurcharges(Long saleRequestId, List<SurchargeTypeDTO> types) {
        MsSaleRequestDTO saleRequestDTO = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        MsSaleRequestSurchargesExtendedDTO surchargesDto = saleRequestSurchargesRepository.saleRequestSurcharges(saleRequestId, SurchargeConverter.toSurchargeTypes(types));
        if (Objects.nonNull(surchargesDto)) {
            List<Currency> currencies = masterdataService.getCurrencies();
            Currency eventCurrency = saleRequestDTO.getEvent().getCurrencyId() != null
                    ? CurrenciesUtils.getCurrencyByCurrencyId(saleRequestDTO.getEvent().getCurrencyId(), currencies)
                    : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(saleRequestDTO.getEvent().getEntity().getId()));
            return SurchargeConverter.toSaleRequestSurchargeDTO(surchargesDto.getSurcharges(), currencies,eventCurrency);
        }
        return new ArrayList<>();
    }

    public void updateSaleRequestSurcharges(Long saleRequestId, SurchargeListDTO surcharges) {
        MsSaleRequestDTO msSaleRequestDTO = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        Set<String> requestCurrencies = surcharges.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());

        if(requestCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        List<Currency> currencies = masterdataService.getCurrencies();
        Currency eventCurrency = msSaleRequestDTO.getEvent().getCurrencyId() != null
                ? CurrenciesUtils.getCurrencyByCurrencyId(msSaleRequestDTO.getEvent().getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(msSaleRequestDTO.getEvent().getEntity().getId()));

        if(requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(eventCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<Surcharge> requests = surcharges.stream()
                .map(surcharge -> SurchargeConverter.fromDTO(surcharge, currencies, eventCurrency))
                .collect(Collectors.toList());

        saleRequestSurchargesRepository.updateSaleRequestSurcharges(saleRequestId, requests);
    }

}
