package es.onebox.mgmt.events.rates;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.converter.RateConverter;
import es.onebox.mgmt.events.converter.RatesConverter;
import es.onebox.mgmt.events.dto.CreateEventRateRequestDTO;
import es.onebox.mgmt.events.dto.EventRateDTO;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.events.dto.UpdateEventRateDTO;
import es.onebox.mgmt.events.dto.UpdateRateDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.validation.RateRestrictionsValidator;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventRatesService {

    private final EventsRepository eventsRepository;
    private final ValidationService validationService;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    public EventRatesService(EventsRepository eventsRepository, ValidationService validationService, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public List<EventRateDTO> getRates(Long eventId) {

        validationService.getAndCheckEvent(eventId);

        EventRates rates = eventsRepository.getEventRates(eventId);
        return RatesConverter.fromMsEvent(rates);
    }

    public IdDTO createRate(Long eventId, CreateEventRateRequestDTO createEventRateRequestDTO) {

        Event event = validationService.getAndCheckEvent(eventId);

        checkAvetEvent(event);

        if (createEventRateRequestDTO.getTexts() != null) {
            checkLanguages(createEventRateRequestDTO.getTexts().getName().keySet(), event);
        }

        IdDTO id = new IdDTO(eventsRepository.createEventRate(eventId, RateConverter.toMsEvent(createEventRateRequestDTO)));
        checkAndProcessExternalEventRates(event, List.of(id.getId()));
        return id;
    }


    public void updateRates(Long eventId, List<UpdateEventRateDTO> ratesDTO) {

        Event event = validationService.getAndCheckEventExternal(eventId);

        checkAvetEvent(event);
        checkPositions(ratesDTO, eventId);

        List<Rate> rates = ratesDTO.stream().
                peek(r -> {
                    if (r.getTexts() != null) {
                        checkLanguages(r.getTexts().getName().keySet(), event);
                    }
                }).
                map(RateConverter::toMsEvent).collect(Collectors.toList());

        eventsRepository.updateEventRates(eventId, rates);
        checkAndProcessExternalEventRates(event, ratesDTO.stream().map(UpdateEventRateDTO::getId).toList());
    }

    private void checkPositions(List<UpdateEventRateDTO> ratesDTO, Long eventId) {
         if (CollectionUtils.isNotEmpty(ratesDTO)) {
             List<Integer> positionList = ratesDTO.stream().map(UpdateEventRateDTO::getPosition)
                 .filter(Objects::nonNull).distinct().toList();

             if (CollectionUtils.isEmpty(positionList)) {
                 return;
             }

             EventRates eventRates = eventsRepository.getEventRates(eventId);

             if (CollectionUtils.isEmpty(eventRates.getData()) || eventRates.getData().size() != ratesDTO.size()) {
                 throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "It is needed to inform all the rates to change the positions", null);
             }

             ratesDTO.forEach(rateDTO -> {
                 if (rateDTO.getPosition() == null) {
                     throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Position is null on rate id: " + rateDTO.getId(), null);
                 }
            });

             if (positionList.size() != ratesDTO.size()) {
                 throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Repeated position on rates", null);
             }
        }
    }

    public void updateRate(Long eventId, Long rateId, UpdateRateDTO rateData) {

        Event event = validationService.getAndCheckEventExternal(eventId);

        checkAvetEvent(event);

        if (rateData.getTexts() != null) {
            checkLanguages(rateData.getTexts().getName().keySet(), event);
        }

        eventsRepository.updateEventRate(eventId, rateId, RateConverter.toMsEvent(rateData));
        checkAndProcessExternalEventRates(event, List.of(rateId));
    }

    public void deleteRate(Long eventId, Long rateId) {

        Event event = validationService.getAndCheckEventExternal(eventId);

        checkAvetEvent(event);

        eventsRepository.deleteEventRate(eventId, rateId);
    }

    public RateRestrictionDTO getRateRestrictions(Long eventId, Long rateId) {

        validationService.getAndCheckEvent(eventId);

        RateRestrictions restriction = eventsRepository.getRateRestrictions(eventId, rateId);
        return RateConverter.fromMsEvent(restriction);
    }

    public void updateRateRestrictions(Long eventId, Long rateId, RateRestrictionDTO restrictionDTO) {

        Event event = validationService.getAndCheckEvent(eventId);

        RateRestrictionsValidator.validateRestrictions(event, restrictionDTO);

        eventsRepository.updateRateRestrictions(eventId, rateId, RateConverter.toMsEvent(restrictionDTO));
    }

    public void deleteEventRateRestrictions(Long eventId, Long rateId) {

        validationService.getAndCheckEvent(eventId);

        eventsRepository.deleteEventRateRestrictions(eventId, rateId);
    }

    public RatesRestrictedDTO getRestrictedRates(Long eventId) {
        validationService.getAndCheckEvent(eventId);

        List<RateRestricted> restrictionsData = eventsRepository.getRestrictedRates(eventId);
        return RateConverter.fromMsEvent(restrictionsData);
    }

    public List<IdNameCodeDTO> getRatesExternalTypes(Long eventId) {
        validationService.getAndCheckEvent(eventId);

        return eventsRepository.getRatesExternalTypes(eventId);
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

    private static void checkAvetEvent(Event event) {
        if (EventType.AVET.equals(event.getType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "AVET event rates cannot be created/modified/deleted ", null);
        }
    }

    private void checkAndProcessExternalEventRates(Event event, List<Long> rateIds) {
        if (event == null || CollectionUtils.isEmpty(event.getVenues())) {
            return;
        }
        List<Long> venueIds = event.getVenues().stream().map(Venue::getId).distinct().toList();
        List<AccessControlSystem> accessControlSystems = new ArrayList<>();
        venueIds.forEach(venueId -> {
            List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);
            if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
                accessControlSystems.addAll(venueAccessControlSystems);
            }
        });

        if (CollectionUtils.isNotEmpty(accessControlSystems)) {
            accessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

                if (externalAccessControlHandler == null) {
                    return;
                }
                rateIds.forEach(rateId -> externalAccessControlHandler.addOrUpdateEventRate(event.getEntityId(), event.getId(), rateId));
            });
        }
    }
}
