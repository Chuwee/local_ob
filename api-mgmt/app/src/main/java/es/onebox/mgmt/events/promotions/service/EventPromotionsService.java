package es.onebox.mgmt.events.promotions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionsFilter;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.EventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.UpdateEventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.repository.EntityPromotionsRepository;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.events.promotions.converter.EventPromotionConverter;
import es.onebox.mgmt.events.promotions.converter.EventPromotionScopeConverter;
import es.onebox.mgmt.events.promotions.dto.CreateEventPromotionDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionSessionsDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionSessionsDTO;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static es.onebox.mgmt.events.promotions.converter.EventPromotionConverter.toMsPromotions;

@Service
public class EventPromotionsService {

    private final ValidationService validationService;
    private final EventPromotionsRepository eventPromotionsRepository;
    private final EntityPromotionsRepository entityPromotionsRepository;
    private final MasterdataService masterdataService;

	@Autowired
    public EventPromotionsService(EventPromotionsRepository eventPromotionsRepository, ValidationService validationService,
                                  EntityPromotionsRepository entityPromotionsRepository, MasterdataService masterdataService) {
		this.eventPromotionsRepository = eventPromotionsRepository;
		this.validationService = validationService;
        this.entityPromotionsRepository = entityPromotionsRepository;
        this.masterdataService = masterdataService;
    }

    public EventPromotionsDTO getEventPromotions(Long eventId, PromotionsFilter filter) {
        validationService.getAndCheckEvent(eventId);
        PromotionTemplates promotionTemplates = eventPromotionsRepository.getEventPromotions(eventId, filter);
        return EventPromotionConverter.fromMsPromotions(promotionTemplates);
    }

    public EventPromotionDetailDTO getEventPromotion(Long eventId, Long promotionId) {
        validationService.getAndCheckEvent(eventId);
        return EventPromotionConverter
                .fromMsPromotion(eventPromotionsRepository.getEventPromotion(eventId, promotionId), masterdataService.getCurrencies());
    }

    public void updateEventPromotionDetail(Long eventId, Long promotionId, UpdateEventPromotionDetailDTO body) {
        validationService.getAndCheckEvent(eventId);
        UpdateEventPromotionDetail requestBody = EventPromotionConverter.toUpdatePromotionDetail(body, masterdataService.getCurrencies());
        this.eventPromotionsRepository.updateEventPromotion(eventId, promotionId, requestBody);
    }

    public IdDTO createEventPromotion(Long eventId, CreateEventPromotionDTO createEventPromotionDTO) {
        validationService.getAndCheckEvent(eventId);
        Long entityTemplateId = createEventPromotionDTO.getFromEntityTemplateId();
        if (entityTemplateId != null) {
            entityPromotionsRepository.getEventPromotionTemplateDetail(entityTemplateId);
            ClonePromotion msDto = EventPromotionConverter.toClonePromotion(createEventPromotionDTO);
            return this.eventPromotionsRepository
                    .cloneEventPromotionFromEntityTemplate(eventId, Collections.singletonList(msDto)).get(0);
        }
        if (createEventPromotionDTO.getType() == null) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.EVENT_PROMOTION_TYPE_MANDATORY);
        }
        return eventPromotionsRepository
                .createEventPromotion(eventId, toMsPromotions(createEventPromotionDTO));
    }

    public IdDTO cloneEventPromotion(Long eventId, Long eventPromotionId) {
        validationService.getAndCheckEvent(eventId);
        ClonePromotion msDto = EventPromotionConverter.toClonePromotion(eventPromotionId);
        return eventPromotionsRepository
                .cloneEventPromotion(eventId, Collections.singletonList(msDto)).get(0);
    }

    public void deleteEventPromotion(Long eventId, Long eventPromotionId) {
        validationService.getAndCheckEvent(eventId);
        eventPromotionsRepository.deleteEventPromotion(eventId, eventPromotionId);
    }

	public PromotionChannelsDTO getEventPromotionChannels(Long eventId, Long eventPromotionId) {
	    validationService.getAndCheckEvent(eventId);
        EventPromotionChannels scopes = eventPromotionsRepository.getEventPromotionsChannels(eventId, eventPromotionId);
		return EventPromotionScopeConverter.toChannelsApi(scopes);
	}

	public void updateEventPromotionChannels(Long eventId, Long eventPromotionId, UpdatePromotionChannelsDTO body) {
	    validationService.getAndCheckEvent(eventId);
		UpdateEventPromotionChannels scopes = EventPromotionScopeConverter.toChannelsMs(body);
		this.eventPromotionsRepository.updateEventPromotionChannels(eventId, eventPromotionId, scopes);
	}

    public EventPromotionSessionsDTO getEventPromotionSessions(Long eventId, Long eventPromotionId) {
        validationService.getAndCheckEvent(eventId);
        EventPromotionSessions scopes = eventPromotionsRepository.getEventPromotionsSessions(eventId, eventPromotionId);
        return EventPromotionScopeConverter.toSessionApi(scopes);
    }

    public void updateEventPromotionSessions(Long eventId, Long eventPromotionId, UpdateEventPromotionSessionsDTO body) {
        validationService.getAndCheckEvent(eventId);
        UpdateEventPromotionSessions scopes = EventPromotionScopeConverter.toSessionMs(body);
        this.eventPromotionsRepository.updateEventPromotionSessions(eventId, eventPromotionId, scopes);
    }

    public EventPromotionPriceTypesDTO getEventPromotionPriceTypes(Long eventId, Long eventPromotionId) {
        validationService.getAndCheckEvent(eventId);
        EventPromotionPriceTypes scopes = eventPromotionsRepository.getEventPromotionsPriceTypes(eventId,
                eventPromotionId);
        return EventPromotionScopeConverter.toPriceTypesApi(scopes);
    }

    public void updateEventPromotionPriceTypes(Long eventId, Long eventPromotionId,
            UpdateEventPromotionPriceTypesDTO body) {
        validationService.getAndCheckEvent(eventId);
        UpdateEventPromotionPriceTypes scopes = EventPromotionScopeConverter.toPriceTypesMs(body);
        this.eventPromotionsRepository.updateEventPromotionPriceTypes(eventId, eventPromotionId, scopes);
    }

    public EventPromotionRatesDTO getEventPromotionRates(Long eventId, Long eventPromotionId) {
        validationService.getAndCheckEvent(eventId);
        EventPromotionRates scopes = eventPromotionsRepository.getEventPromotionsRates(eventId, eventPromotionId);
        return EventPromotionScopeConverter.toRatesApi(scopes);
    }

    public void updateEventPromotionRates(Long eventId, Long eventPromotionId, UpdateEventPromotionRatesDTO body) {
        validationService.getAndCheckEvent(eventId);
        UpdateEventPromotionRates scopes = EventPromotionScopeConverter.toRatesMs(body);
        this.eventPromotionsRepository.updateEventPromotionRates(eventId, eventPromotionId, scopes);
    }

    public EventPromotionPacksDTO getEventPromotionPacks(Long eventId, Long promotionId) {
        validationService.getAndCheckEvent(eventId);
        EventPromotionPacks scopes = eventPromotionsRepository.getEventPromotionsPacks(eventId, promotionId);
        return EventPromotionScopeConverter.toPacksApi(scopes);
    }

    public void updateEventPromotionPacks(Long eventId, Long promotionId, UpdateEventPromotionPacksDTO body) {
        validationService.getAndCheckEvent(eventId);
        UpdateEventPromotionPacks scopes = EventPromotionScopeConverter.toPacksMs(body);
        eventPromotionsRepository.updateEventPromotionPacks(eventId, promotionId, scopes);
    }
}
