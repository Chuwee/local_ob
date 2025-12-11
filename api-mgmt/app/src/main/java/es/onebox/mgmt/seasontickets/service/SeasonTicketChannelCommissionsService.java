package es.onebox.mgmt.seasontickets.service;

import es.onebox.mgmt.channels.commissions.converter.CommissionConverter;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestCommissionsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
public class SeasonTicketChannelCommissionsService {


    private final SaleRequestsRepository saleRequestsRepository;
    private final SeasonTicketValidationService stValidationService;
    private final SaleRequestCommissionsRepository saleRequestCommissionsRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SeasonTicketChannelCommissionsService(SaleRequestsRepository saleRequestsRepository, SeasonTicketValidationService stValidationService,
                                                 SaleRequestCommissionsRepository saleRequestCommissionsRepository,
                                                 MasterdataService masterdataService, EntitiesRepository entitiesRepository){

        this.saleRequestsRepository = saleRequestsRepository;
        this.stValidationService = stValidationService;
        this.saleRequestCommissionsRepository = saleRequestCommissionsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;

    }

    public List<CommissionDTO> getChannelCommissions(Long seasonTicketId, Long channelId, List<CommissionTypeDTO> types) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(seasonTicketId));
        filter.setStatus(Collections.singletonList(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if (nonNull(searchSaleRequest) && CollectionUtils.isNotEmpty(searchSaleRequest.getData())) {
            Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
            if(optSaleRequestId.isPresent()) {
                Long saleRequestId = optSaleRequestId.get().getId();
                List<ChannelCommission> msCommissions = saleRequestCommissionsRepository.getSaleRequestCommissions(saleRequestId, types);
                if (CollectionUtils.isNotEmpty(msCommissions)) {
                    List<Currency> currencies = masterdataService.getCurrencies();
                    Long entityId = optSaleRequestId.get().getEvent().getEntity().getId();
                    Currency seasonTicketCurrency = optSaleRequestId.get().getEvent().getCurrencyId() != null
                            ? CurrenciesUtils.getCurrencyByCurrencyId(optSaleRequestId.get().getEvent().getCurrencyId(), currencies)
                            : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(entityId));
                    return CommissionConverter.fromMsChannelsCommissionRangeResponse(msCommissions, currencies, seasonTicketCurrency);
                }
            }
        }
        return new ArrayList<>();
    }
}
