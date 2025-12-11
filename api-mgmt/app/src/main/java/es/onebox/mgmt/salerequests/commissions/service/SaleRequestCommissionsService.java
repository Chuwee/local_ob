package es.onebox.mgmt.salerequests.commissions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.commissions.converter.CommissionConverter;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionListDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestCommissionsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleRequestCommissionsService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestCommissionsRepository saleRequestCommissionsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SaleRequestCommissionsService(SecurityManager securityManager, SaleRequestsRepository saleRequestsRepository,
                                         SaleRequestCommissionsRepository saleRequestCommissionsRepository,
                                         MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.saleRequestsRepository = saleRequestsRepository;
        this.saleRequestCommissionsRepository = saleRequestCommissionsRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<CommissionDTO> saleRequestCommissions(Long saleRequestId, List<CommissionTypeDTO> types) {
        MsSaleRequestDTO saleRequestDTO = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        List<ChannelCommission> msCommissions = saleRequestCommissionsRepository.getSaleRequestCommissions(saleRequestId, types);

        if (CollectionUtils.isNotEmpty(msCommissions)) {
            List<Currency> currencies = masterdataService.getCurrencies();
            Currency eventCurrency = saleRequestDTO.getEvent().getCurrencyId() != null
                    ? CurrenciesUtils.getCurrencyByCurrencyId(saleRequestDTO.getEvent().getCurrencyId(), currencies)
                    : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(saleRequestDTO.getEvent().getEntity().getId()));
            return CommissionConverter.fromMsChannelsCommissionRangeResponse(msCommissions,currencies, eventCurrency);
        }

        return new ArrayList<>();
    }

    public void updateSaleRequestCommissions(Long saleRequestId, CommissionListDTO commissionListDto) {
        MsSaleRequestDTO msSaleRequestDTO = SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        Set<String> requestCurrencies =  commissionListDto.stream().map(CommissionDTO::getRanges)
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

        List<ChannelCommission> requests = commissionListDto.stream()
                .map(channelCommission -> CommissionConverter.fromDTO(channelCommission, currencies,eventCurrency))
                .collect(Collectors.toList());

        saleRequestCommissionsRepository.updateSaleRequestCommissions(saleRequestId, requests);
    }
}
