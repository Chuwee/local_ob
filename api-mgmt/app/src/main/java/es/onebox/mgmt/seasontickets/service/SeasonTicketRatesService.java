package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRates;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.events.converter.RateConverter;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketRatesConverter;
import es.onebox.mgmt.seasontickets.dto.rates.CreateSeasonTicketRateRequestDTO;
import es.onebox.mgmt.seasontickets.dto.rates.SeasonTicketRateDTO;
import es.onebox.mgmt.seasontickets.dto.rates.UpdateSeasonTicketRateDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.RateRestrictionsValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;

@Service
public class SeasonTicketRatesService {

    @Autowired
    private SeasonTicketRepository seasonTicketRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private AccessControlSystemsRepository accessControlSystemsRepository;
    @Autowired
    private ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    public List<SeasonTicketRateDTO> getRates(Long seasonTicketId) {

        checkSeasonTicketAccessibility(seasonTicketId);

        SeasonTicketRates rates = seasonTicketRepository.getSeasonTicketRates(seasonTicketId.intValue());
        return SeasonTicketRatesConverter.fromMsEvent(rates);
    }

    public IdDTO createRate(Long seasonTicketId, CreateSeasonTicketRateRequestDTO createSeasonTicketRateRequestDTO) {

        SeasonTicket seasonTicket = checkSeasonTicketAccessibility(seasonTicketId);

        if (createSeasonTicketRateRequestDTO.getTexts() != null) {
            checkLanguages(createSeasonTicketRateRequestDTO.getTexts().getName().keySet(), seasonTicket);
        }

        Long rateId = seasonTicketRepository.createSeasonTicketRate(seasonTicketId,
                SeasonTicketRatesConverter.toMsEvent(createSeasonTicketRateRequestDTO));

        if (BooleanUtils.isTrue(seasonTicket.getAllowChangeSeat())) {
            seasonTicketChangeSeatsService.createChangeSeatPricesTable(seasonTicketId, rateId);
        }

        checkAndProcessExternalSeasonTicketRates(seasonTicket, List.of(rateId));

        return new IdDTO(rateId);
    }

    public void updateRates(Long seasonTicketId, List<UpdateSeasonTicketRateDTO> ratesDTO) {

        SeasonTicket seasonTicket = checkSeasonTicketAccessibility(seasonTicketId);

        checkPositions(ratesDTO, seasonTicketId);
        List<SeasonTicketRate> rates = ratesDTO.stream().
                peek(r -> {
                    if (r.getTexts() != null) {
                        checkLanguages(r.getTexts().getName().keySet(), seasonTicket);
                    }
                }).
                map(SeasonTicketRatesConverter::toMsEvent).collect(Collectors.toList());

        seasonTicketRepository.updateSeasonTicketRates(seasonTicketId, rates);
        checkAndProcessExternalSeasonTicketRates(seasonTicket, ratesDTO.stream().map(UpdateSeasonTicketRateDTO::getId).toList());

    }

    public void updateRate(Long eventId, Long rateId, UpdateSeasonTicketRateDTO rateData) {

        SeasonTicket seasonTicket = checkSeasonTicketAccessibility(eventId);

        if (rateData.getTexts() != null) {
            checkLanguages(rateData.getTexts().getName().keySet(), seasonTicket);
        }

        SeasonTicketRate rate = SeasonTicketRatesConverter.toMsEvent(rateData);
        seasonTicketRepository.updateSeasonTicketRate(eventId, rateId, rate);
        checkAndProcessExternalSeasonTicketRates(seasonTicket, List.of(rateId));
    }

    public void deleteRate(Long seasonTicketId, Long rateId) {

        checkSeasonTicketAccessibility(seasonTicketId);

        seasonTicketRepository.deleteSeasonTicketRate(seasonTicketId, rateId);
    }

    private void checkLanguages(Set<String> languageKeys, SeasonTicket seasonTicket) {
        for (String languageKey : languageKeys) {
            String locale = ConverterUtils.toLocale(languageKey);
            if (seasonTicket.getLanguages().stream().noneMatch(l -> l.getCode().equals(locale))) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageKey +
                        " for Season Ticket: " + seasonTicket.getId(), null);
            }
        }
    }

    private SeasonTicket checkSeasonTicketAccessibility(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw OneboxRestException.builder(BAD_REQUEST_PARAMETER).setMessage("Season Ticket id is mandatory").build();
        }
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw OneboxRestException.builder(NOT_FOUND)
                    .setMessage("no season ticket found with id: " + seasonTicketId)
                    .build();
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());

        return seasonTicket;
    }

    private void checkPositions(List<UpdateSeasonTicketRateDTO> ratesDTO, Long seasonTicketId) {
        if (CollectionUtils.isNotEmpty(ratesDTO)) {
            List<Integer> positionList = ratesDTO.stream().map(UpdateSeasonTicketRateDTO::getPosition)
                    .filter(Objects::nonNull).distinct().toList();

            if (CollectionUtils.isEmpty(positionList)) {
                return;
            }

            SeasonTicketRates seasonTicketRates = seasonTicketRepository.getSeasonTicketRates(seasonTicketId.intValue());

            if (CollectionUtils.isEmpty(seasonTicketRates.getData()) || seasonTicketRates.getData().size() != ratesDTO.size()) {
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

    public RateRestrictionDTO getRateRestrictions(Long seasonTicketId, Long rateId) {

        checkSeasonTicketAccessibility(seasonTicketId);

        RateRestrictions restriction = eventsRepository.getRateRestrictions(seasonTicketId, rateId);
        return RateConverter.fromMsEvent(restriction);
    }

    public void updateRateRestrictions(Long seasonTicketId, Long rateId, RateRestrictionDTO restrictionDTO) {

        checkSeasonTicketAccessibility(seasonTicketId);

        RateRestrictionsValidator.validateSeasonTicketRestrictions(restrictionDTO);

        eventsRepository.updateRateRestrictions(seasonTicketId, rateId, RateConverter.toMsEvent(restrictionDTO));
    }

    public void deleteSeasonTicketRateRestrictions(Long seasonTicketId, Long rateId) {

        checkSeasonTicketAccessibility(seasonTicketId);

        eventsRepository.deleteEventRateRestrictions(seasonTicketId, rateId);
    }

    public RatesRestrictedDTO getRestrictedRates(Long seasonTicketId) {

        checkSeasonTicketAccessibility(seasonTicketId);

        List<RateRestricted> restrictionsData = eventsRepository.getRestrictedRates(seasonTicketId);
        return RateConverter.fromMsEvent(restrictionsData);
    }

    private void checkAndProcessExternalSeasonTicketRates(SeasonTicket seasonTicket, List<Long> rateIds) {
        if (seasonTicket == null || CollectionUtils.isEmpty(seasonTicket.getVenues())) {
            return;
        }
        List<Long> venueIds = seasonTicket.getVenues().stream().map(Venue::getId).distinct().toList();
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
                rateIds.forEach(rateId -> externalAccessControlHandler.addOrUpdateEventRate(seasonTicket.getEntityId(), seasonTicket.getId(), rateId));
            });
        }
    }
}
