package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionsFilter;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.repository.EntityPromotionsRepository;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.events.promotions.converter.EventPromotionConverter;
import es.onebox.mgmt.events.promotions.converter.EventPromotionScopeConverter;
import es.onebox.mgmt.events.promotions.dto.CreateEventPromotionDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionRatesDTO;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static es.onebox.mgmt.events.promotions.converter.EventPromotionConverter.toMsPromotions;

@Service
public class SeasonTicketPromotionsService {

    @Autowired
    private EventPromotionsRepository eventPromotionsRepository;
    @Autowired
    private EntityPromotionsRepository entityPromotionsRepository;
    @Autowired
    private SeasonTicketValidationService validationService;
    @Autowired
    private SeasonTicketRepository seasonTicketRepository;
    @Autowired
    private MasterdataService masterdataService;

    public EventPromotionsDTO getSeasonTicketPromotions(Long seasonTicketId, PromotionsFilter filter) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        PromotionTemplates promotionTemplates = eventPromotionsRepository.getEventPromotions(seasonTicketId, filter);
        return EventPromotionConverter.fromMsPromotions(promotionTemplates);
    }

    public EventPromotionDetailDTO getSeasonTicketPromotion(Long seasonTicketId, Long promotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        return EventPromotionConverter
                .fromMsPromotion(eventPromotionsRepository.getEventPromotion(seasonTicketId, promotionId), masterdataService.getCurrencies());
    }

    public void updateSeasonTicketPromotionDetail(Long seasonTicketId, Long promotionId, UpdateEventPromotionDetailDTO body) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateEventPromotionDetail requestBody = EventPromotionConverter.toUpdatePromotionDetail(body, masterdataService.getCurrencies());
        this.eventPromotionsRepository.updateEventPromotion(seasonTicketId, promotionId, requestBody);
    }

    public IdDTO createSeasonTicketPromotion(Long seasonTicketId, CreateEventPromotionDTO createEventPromotionDTO) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        Long entityTemplateId = createEventPromotionDTO.getFromEntityTemplateId();
        if (entityTemplateId != null) {
            entityPromotionsRepository.getEventPromotionTemplateDetail(entityTemplateId);
            ClonePromotion msDto = EventPromotionConverter.toClonePromotion(createEventPromotionDTO);
            return this.eventPromotionsRepository
                    .cloneEventPromotionFromEntityTemplate(seasonTicketId, Collections.singletonList(msDto)).get(0);
        }
        if (createEventPromotionDTO.getType() == null) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.EVENT_PROMOTION_TYPE_MANDATORY);
        }
        IdDTO newEventPromotion = eventPromotionsRepository
                .createEventPromotion(seasonTicketId, toMsPromotions(createEventPromotionDTO));

        setDefaultSessionType(seasonTicketId, newEventPromotion);

        return newEventPromotion;
    }

    private void setDefaultSessionType(Long seasonTicketId, IdDTO newEventPromotion) {
        UpdateEventPromotionSessions updateEventPromotionSessionsRequest = new UpdateEventPromotionSessions();
        updateEventPromotionSessionsRequest.setType(PromotionTargetType.ALL);
        eventPromotionsRepository.updateEventPromotionSessions(seasonTicketId, newEventPromotion.getId(), updateEventPromotionSessionsRequest);
    }

    public IdDTO cloneSeasonTicketPromotion(Long seasonTicketId, Long eventPromotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        ClonePromotion msDto = EventPromotionConverter.toClonePromotion(eventPromotionId);
        return eventPromotionsRepository
                .cloneEventPromotion(seasonTicketId, Collections.singletonList(msDto)).get(0);
    }

    public void deleteSeasonTicketPromotion(Long seasonTicketId, Long eventPromotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        eventPromotionsRepository.deleteEventPromotion(seasonTicketId, eventPromotionId);
    }

    public PromotionChannelsDTO getSeasonTicketPromotionChannels(Long seasonTicketId, Long eventPromotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        EventPromotionChannels scopes = eventPromotionsRepository.getEventPromotionsChannels(seasonTicketId, eventPromotionId);
        return EventPromotionScopeConverter.toChannelsApi(scopes);
    }

    public void updateSeasonTicketPromotionChannels(Long seasonTicketId, Long eventPromotionId,
                                                    UpdatePromotionChannelsDTO body) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateEventPromotionChannels scopes = EventPromotionScopeConverter.toChannelsMs(body);
        this.eventPromotionsRepository.updateEventPromotionChannels(seasonTicketId, eventPromotionId, scopes);
    }

    public EventPromotionPriceTypesDTO getSeasonTicketPromotionPriceTypes(Long seasonTicketId, Long eventPromotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        EventPromotionPriceTypes scopes = eventPromotionsRepository.getEventPromotionsPriceTypes(seasonTicketId,
                eventPromotionId);
        return EventPromotionScopeConverter.toPriceTypesApi(scopes);
    }

    public void updateSeasonTicketPromotionPriceTypes(Long seasonTicketId, Long eventPromotionId,
                                                      UpdateEventPromotionPriceTypesDTO body) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateEventPromotionPriceTypes scopes = EventPromotionScopeConverter.toPriceTypesMs(body);
        this.eventPromotionsRepository.updateEventPromotionPriceTypes(seasonTicketId, eventPromotionId, scopes);
    }

    public EventPromotionRatesDTO getSeasonTicketsPromotionRates(Long seasonTicketId, Long eventPromotionId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        EventPromotionRates scopes = eventPromotionsRepository.getEventPromotionsRates(seasonTicketId, eventPromotionId);
        return EventPromotionScopeConverter.toRatesApi(scopes);
    }

    public void updateSesonTicketPromotionRates(Long seasonTicketId, Long eventPromotionId, UpdateEventPromotionRatesDTO body) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateEventPromotionRates scopes = EventPromotionScopeConverter.toRatesMs(body);
        this.eventPromotionsRepository.updateEventPromotionRates(seasonTicketId, eventPromotionId, scopes);
    }
}
