package es.onebox.mgmt.events.ratesgroup;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroupType;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatesGroup;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.converter.RateGroupsConverter;
import es.onebox.mgmt.events.dto.CreateEventRatesGroupRequestDTO;
import es.onebox.mgmt.events.dto.RateGroupDTO;
import es.onebox.mgmt.events.dto.UpdateRateGroupDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventRatesGroupService {

    private final EventsRepository eventsRepository;
    private final ValidationService validationService;
    
    protected EventRatesGroupService(EventsRepository eventsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public List<RateGroupDTO> getRatesGroup(Long eventId, RateGroupType type) {

        validationService.checkAvetEventAccessibility(eventId);

        RatesGroup rates = eventsRepository.getEventRatesGroup(eventId, type);
        return RateGroupsConverter.fromMsEvent(rates);
    }

    public IdDTO createRateGroups(Long eventId, CreateEventRatesGroupRequestDTO createEventRatesGroupRequestDTO) {

        Event event = validationService.checkAvetEventAccessibility(eventId);

        if (createEventRatesGroupRequestDTO.getTexts() != null) {
            checkLanguages(createEventRatesGroupRequestDTO.getTexts().getName().keySet(), event);
        }

        return new IdDTO(eventsRepository.createEventRateGroup(eventId, RateGroupsConverter.toMsEvent(createEventRatesGroupRequestDTO)));
    }

    public void updateRatesGroup(Long eventId, List<UpdateRateGroupDTO> ratesDTO) {

        Event event = validationService.checkAvetEventAccessibility(eventId);

        List<RateGroup> rates = ratesDTO.stream().
                peek(r -> {
                    if (r.getTexts() != null) {
                        checkLanguages(r.getTexts().getName().keySet(), event);
                    }
                }).
                map(RateGroupsConverter::toMsEvent).collect(Collectors.toList());

        eventsRepository.updateEventRatesGroup(eventId, rates);
    }

    public void updateRateGroup(Long eventId, Long rateId, UpdateRateGroupDTO rateData) {

        Event event = validationService.checkAvetEventAccessibility(eventId);
        if (rateData.getTexts() != null) {
            checkLanguages(rateData.getTexts().getName().keySet(), event);
        }

        eventsRepository.updateEventRateGroup(eventId, rateId, RateGroupsConverter.toMsEvent(rateData));
    }

    public void deleteRateGroup(Long eventId, Long rateId) {
        eventsRepository.deleteEventRateGroup(eventId, rateId);
    }

    private static void checkLanguages(Set<String> languageKeys, Event event) {
        for (String languageKey : languageKeys) {
            String locale = ConverterUtils.toLocale(languageKey);
            if (event.getLanguages().stream().noneMatch(l -> l.getCode().equals(locale))) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageKey +
                        " for event: " + event.getId(), null);
            }
        }
    }

}
