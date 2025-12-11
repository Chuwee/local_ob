package es.onebox.mgmt.events.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.CommonSurchargeService;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventSurchargeService {

    private final EventsRepository eventsRepository;
    private final SecurityManager securityManager;
    private final CommonSurchargeService commonSurchargeService;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public EventSurchargeService(EventsRepository eventsRepository, SecurityManager securityManager,
                                 CommonSurchargeService commonSurchargeService, MasterdataService masterdataService,
                                 EntitiesRepository entitiesRepository) {
        this.eventsRepository = eventsRepository;
        this.securityManager = securityManager;
        this.commonSurchargeService = commonSurchargeService;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public void setSurcharge(Long eventId, EventSurchargeListDTO eventSurchargeListDTO) {
        Event event = checkEventPermissions(eventId);

        commonSurchargeService.validateSurcharges(eventSurchargeListDTO);

        Set<String> requestCurrencies = eventSurchargeListDTO.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(requestCurrencies.size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        List<Currency> currencies = masterdataService.getCurrencies();
        Currency eventCurrency = event.getCurrencyId()!= null
                ? CurrenciesUtils.getCurrencyByCurrencyId(event.getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(event.getEntityId()));
        if(requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(eventCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<EventSurcharge> requests = eventSurchargeListDTO.stream()
                .map(eventSurchargeDTO -> SurchargeConverter.fromDTO(eventSurchargeDTO, currencies, eventCurrency))
                .collect(Collectors.toList());

        eventsRepository.setSurcharge(eventId, requests);
    }

    public List<EventSurchargeDTO> getSurcharges(Long eventId, List<SurchargeTypeDTO> types) {
        Event event = checkEventPermissions(eventId);

        List<EventSurcharge> eventRanges = eventsRepository.getSurcharges(eventId, types);

        List<Currency> currencies = masterdataService.getCurrencies();
        Currency eventCurrency = event.getCurrencyId() != null
                ? CurrenciesUtils.getCurrencyByCurrencyId(event.getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(event.getEntityId()));
        return SurchargeConverter.toEventSurchargeDTO(eventRanges, currencies, eventCurrency);
    }

    private Event checkEventPermissions(Long eventId) {
        Event event = eventsRepository.getEvent(eventId);

        if (event == null || event.getStatus() == EventStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        return event;
    }
}
