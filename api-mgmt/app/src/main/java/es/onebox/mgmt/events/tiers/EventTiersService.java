package es.onebox.mgmt.events.tiers;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCapacity;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.events.converter.TierConverter;
import es.onebox.mgmt.events.dto.CreateEventTierRequestDTO;
import es.onebox.mgmt.events.dto.EventTierFilter;
import es.onebox.mgmt.events.dto.TierChannelContentFilter;
import es.onebox.mgmt.events.dto.TierChannelContentsListDTO;
import es.onebox.mgmt.events.dto.TierDTO;
import es.onebox.mgmt.events.dto.TierExtendedDTO;
import es.onebox.mgmt.events.dto.TiersDTO;
import es.onebox.mgmt.events.dto.UpdateTierRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtTierErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventTiersService {

    private final EventsRepository eventsRepository;
    private final MasterdataService masterdataService;
    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;

    protected EventTiersService(EventsRepository eventsRepository, MasterdataService masterdataService, VenuesRepository venuesRepository,
            ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.masterdataService = masterdataService;
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
    }

    public IdDTO createTier(Long eventId, CreateEventTierRequestDTO createTierReqDTO) {

        validationService.getAndCheckEventExternal(eventId);

        if (createTierReqDTO == null) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_DATA_MANDATORY);
        }

        if (createTierReqDTO.getName() == null || createTierReqDTO.getName().isEmpty()) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_NAME_MANDATORY);
        }
        if (createTierReqDTO.getPrice() == null || createTierReqDTO.getPrice() < 0) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_PRICE_MANDATORY);
        }
        if (createTierReqDTO.getPriceTypeId() == null || createTierReqDTO.getPriceTypeId() <= 0) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_PRICE_TYPE_MANDATORY);
        }
        if (createTierReqDTO.getStartDate() == null) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_START_DATE_MANDATORY);
        }

        return new IdDTO(eventsRepository.createEventTier(eventId, TierConverter.toMsEvent(createTierReqDTO)));
    }

    public TiersDTO getEventTiers(Long eventId, EventTierFilter eventTierFilter) {
        validationService.getAndCheckEvent(eventId);
        HashMap<Long, Long> capacitiesByTiers = new HashMap<>();
        if (eventTierFilter.getVenueTemplateId() != null) {
            List<PriceTypeCapacity> ptcs = venuesRepository.getPriceTypeCapacity(eventTierFilter.getVenueTemplateId());
            capacitiesByTiers.putAll(ptcs.stream()
                    .collect(Collectors.toMap(PriceTypeCapacity::getId, PriceTypeCapacity::getCapacity)));
        }
        TiersDTO tiers = TierConverter.fromMsEvent(eventsRepository.getEventTiers(eventId,
                eventTierFilter.getVenueTemplateId(), eventTierFilter.getActive(), eventTierFilter.getLimit(),
                eventTierFilter.getOffset()));
        if(!capacitiesByTiers.isEmpty()){
            tiers.getData().forEach(t -> t.getPriceType().setCapacity(capacitiesByTiers.get(t.getPriceType().getId())));
        }
        return tiers;
    }

    public TierExtendedDTO getEventTier(Long eventId, Long tierId) {
        validationService.getAndCheckEvent(eventId);
        validateTierId(tierId);
        return TierConverter.fromMsEvent(eventsRepository.getEventTier(eventId, tierId));
    }


    public TierDTO updateEventTier(Long eventId, Long tierId, UpdateTierRequestDTO updateTierRequestDTO) {
        if (updateTierRequestDTO == null) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_DATA_MANDATORY);
        }
        validationService.getAndCheckEventExternal(eventId);
        validateTierId(tierId);
        return TierConverter.fromMsEvent(eventsRepository.updateEventTier(eventId, tierId,
                TierConverter.toMsEvent(updateTierRequestDTO)));
    }

    public void deleteEventTier(Long eventId, Long tierId) {
        validationService.getAndCheckEventExternal(eventId);
        validateTierId(tierId);
        eventsRepository.deleteEventTier(eventId, tierId);
    }

    public void deleteEventTierLimit(Long eventId, Long tierId) {
        validationService.getAndCheckEventExternal(eventId);
        validateTierId(tierId);
        eventsRepository.deleteEventTierLimit(eventId, tierId);
    }

    public void createEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        validateSaleGroupId(saleGroupId);
        validateLimit(limit);
        validationService.getAndCheckEventExternal(eventId);
        validateTierId(tierId);
        eventsRepository.createEventTierSaleGroup(eventId, tierId, saleGroupId, limit);
    }

    public void updateEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        validateSaleGroupId(saleGroupId);
        validateLimit(limit);
        validationService.getAndCheckEventExternal(eventId);
        validateTierId(tierId);
        eventsRepository.updateEventTierSaleGroup(eventId, tierId, saleGroupId, limit);
    }

    public void deleteEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId) {
        validateSaleGroupId(saleGroupId);
        validationService.getAndCheckEvent(eventId);
        validateTierId(tierId);
        eventsRepository.deleteEventTierSaleGroup(eventId, tierId, saleGroupId);
    }

    public void upsertTierCommElements(Long eventId, Long tierId, TierChannelContentsListDTO commElements) {

        if (commElements == null) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_TRANSLATION_MANDATORY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        commElements.stream().peek(ce -> ConverterUtils.checkLanguage(ce.getLanguage(), languages)).forEach(el -> {
            if (el.getValue().length() > el.getType().getLength()) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.INVALID_COMM_ELEM_VALUE_LENGTH, el.getType().name(),
                        el.getType().getLength());
            }
        });
        validateTierId(tierId);
        validationService.getAndCheckEventExternal(eventId);
        eventsRepository.upsertTierCommElements(eventId, tierId,
                TierConverter.toMsEvent(commElements));
    }

    public TierChannelContentsListDTO getTierCommElements(Long eventId, Long tierId, TierChannelContentFilter filter) {
        validateTierId(tierId);
        validationService.getAndCheckEvent(eventId);
        return TierConverter.convertTierCommElements(eventsRepository.getTierCommElements(eventId, tierId, filter));
    }

    private static void validateLimit(Integer limit) {
        if (limit == null) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.LIMIT_MANDATORY);
        }
        if (limit < 0) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.INVALID_LIMIT);
        }
    }

    private static void validateSaleGroupId(Long saleGroupId) {
        if (saleGroupId == null || saleGroupId <= 0) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.QUOTA_ID_MANDATORY);
        }
    }

    private static void validateTierId(Long tierId) {
        if (tierId == null || tierId < 1) {
            throw new OneboxRestException(ApiMgmtTierErrorCode.TIER_ID_MANDATORY);
        }
    }

}
