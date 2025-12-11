package es.onebox.mgmt.events.eventchannel.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.CommonSurchargeService;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesExtendedDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.eventchannel.EventChannelValidations;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.surcharges.respository.SaleRequestSurchargesRepository;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class EventChannelSurchargesService {

    private final EventsRepository eventsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final SecurityManager securityManager;
    private final CommonSurchargeService commonSurchargeService;
    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestSurchargesRepository saleRequestSurchargesRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public EventChannelSurchargesService(EventsRepository eventsRepository, SecurityManager securityManager,
                                         EventChannelsRepository eventChannelsRepository,
                                         CommonSurchargeService commonSurchargeService,
                                         SaleRequestsRepository saleRequestsRepository,
                                         SaleRequestSurchargesRepository saleRequestSurchargesRepository,
                                         MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.eventsRepository = eventsRepository;
        this.securityManager = securityManager;
        this.eventChannelsRepository = eventChannelsRepository;
        this.commonSurchargeService = commonSurchargeService;
        this.saleRequestsRepository = saleRequestsRepository;
        this.saleRequestSurchargesRepository = saleRequestSurchargesRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<EventSurchargeDTO> getEventChannelSurcharges(Long eventId, Long channelId, List<SurchargeTypeDTO> types) {
        EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        List<EventSurcharge> surcharges = eventChannelsRepository.getEventChannelSurcharges(eventId, channelId, SurchargeConverter.toSurchargeTypes(types));
        if (nonNull(surcharges)) {
            List<Currency> currencies = masterdataService.getCurrencies();
            return SurchargeConverter.toEventSurchargeDTO(surcharges, currencies, getEventCurrency(eventsRepository.getEvent(eventId), currencies));
        }
        return new ArrayList<>();
    }

    public void createEventChannelSurcharges(Long eventId, Long channelId, EventSurchargeListDTO surcharges) {
        EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        commonSurchargeService.validateSurcharges(surcharges);
        Set<String> requestCurrencies = surcharges.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(requestCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }
        List<Currency> currencies = masterdataService.getCurrencies();
        Currency eventCurrency = getEventCurrency(eventsRepository.getEvent(eventId), currencies);
        if(requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(eventCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<EventSurcharge> requests = surcharges.stream()
                .map(eventSurcharge -> SurchargeConverter.fromDTO(eventSurcharge, currencies, eventCurrency))
                .collect(Collectors.toList());

        eventChannelsRepository.setEventChannelSurcharges(eventId, channelId, requests);
    }

    public List<SaleRequestSurchargeDTO> getChannelSurcharges(Long eventId, Long channelId, List<SurchargeTypeDTO> types) {
        EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(eventId));
        filter.setStatus(List.of(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if (nonNull(searchSaleRequest)) {
            Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
            if(optSaleRequestId.isPresent()) {
                Long saleRequestId = optSaleRequestId.get().getId();
                MsSaleRequestSurchargesExtendedDTO surchargesDto = saleRequestSurchargesRepository.saleRequestSurcharges(saleRequestId, SurchargeConverter.toSurchargeTypes(types));
                if (nonNull(surchargesDto) && CollectionUtils.isNotEmpty(surchargesDto.getSurcharges())) {
                    List<Currency> currencies = masterdataService.getCurrencies();
                    Long entityId = optSaleRequestId.get().getEvent().getEntity().getId();
                    Currency eventCurrency = optSaleRequestId.get().getEvent().getCurrencyId() != null
                            ? CurrenciesUtils.getCurrencyByCurrencyId(optSaleRequestId.get().getEvent().getCurrencyId(), currencies)
                            : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(entityId));
                    return SurchargeConverter.toSaleRequestSurchargeDTO(surchargesDto.getSurcharges(), currencies,
                            eventCurrency);
                }
            }
        }
        return new ArrayList<>();
    }

    private Currency getEventCurrency(Event event, List<Currency> currencies) {
        return event.getCurrencyId() != null ?
                CurrenciesUtils.getCurrencyByCurrencyId(event.getCurrencyId(), currencies) : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(event.getEntityId()));
    }

}
