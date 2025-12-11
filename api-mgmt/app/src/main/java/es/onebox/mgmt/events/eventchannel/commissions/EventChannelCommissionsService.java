package es.onebox.mgmt.events.eventchannel.commissions;

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
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.eventchannel.EventChannelValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
public class EventChannelCommissionsService {

    private final EventsRepository eventsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final SecurityManager securityManager;
    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestCommissionsRepository saleRequestCommissionsRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public EventChannelCommissionsService(EventsRepository eventsRepository, SecurityManager securityManager,
                                          EventChannelsRepository eventChannelsRepository,
                                          SaleRequestsRepository saleRequestsRepository,
                                          SaleRequestCommissionsRepository saleRequestCommissionsRepository,
                                          MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.eventsRepository = eventsRepository;
        this.securityManager = securityManager;
        this.eventChannelsRepository = eventChannelsRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.saleRequestCommissionsRepository = saleRequestCommissionsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<CommissionDTO> getChannelCommissions(Long eventId, Long channelId, List<CommissionTypeDTO> types) {
        EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(eventId));
        filter.setStatus(List.of(MsSaleRequestsStatus.ACCEPTED));
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
                    Currency eventCurrency = optSaleRequestId.get().getEvent().getCurrencyId() != null
                            ? CurrenciesUtils.getCurrencyByCurrencyId(optSaleRequestId.get().getEvent().getCurrencyId() , currencies)
                            : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(entityId));
                    return CommissionConverter.fromMsChannelsCommissionRangeResponse(msCommissions, currencies, eventCurrency);
                }
            }
        }
        return new ArrayList<>();
    }
}
