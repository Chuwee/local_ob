package es.onebox.mgmt.events.promotiontemplates.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.PromotionChannelContentTextType;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextFilter;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextListDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.datasources.ms.promotion.repository.EntityPromotionsRepository;
import es.onebox.mgmt.events.promotions.converter.EventPromotionScopeConverter;
import es.onebox.mgmt.events.promotiontemplates.converter.EventPromotionTemplateConverter;
import es.onebox.mgmt.events.promotiontemplates.converter.EventPromotionTemplateFilterConverter;
import es.onebox.mgmt.events.promotiontemplates.dto.CreateEventPromotionTemplateDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateDetailDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateFilter;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplatesDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.UpdateEventPromotionTemplateDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class EventPromotionTemplateService {

    private static final int NAME_MAX_SIZE = 40;
    private static final int DESCRIPTION_MAX_SIZE = 1000;

    private final EntityPromotionsRepository entityPromotionsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public EventPromotionTemplateService(EntityPromotionsRepository entityPromotionsRepository,
                                         SecurityManager securityManager, MasterdataService masterdataService,
                                         EntitiesRepository entitiesRepository) {
        this.securityManager = securityManager;
        this.entityPromotionsRepository = entityPromotionsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public EventPromotionTemplatesDTO getEventPromotionTemplates(final EventPromotionTemplateFilter filter) {
        securityManager.checkEntityAccessible(filter);
        List<Currency> currencies = masterdataService.getCurrencies();
        EventPromotionTemplates templates = entityPromotionsRepository
                .getEventPromotionTemplates(EventPromotionTemplateFilterConverter.convertToMsPromotionTemplateFilter(filter, currencies));
        EventPromotionTemplatesDTO out = EventPromotionTemplateConverter.from(templates.getData(), currencies);
        out.setMetadata(templates.getMetadata());
        return out;
    }

    public EventPromotionTemplateDetailDTO getEventPromotionTemplate(final Long promotionTemplateId) {
        PromotionTemplateDetail promotionTemplateDetail = getAndCheckEventPromotionTemplate(promotionTemplateId);
        return EventPromotionTemplateConverter.from(promotionTemplateDetail, masterdataService.getCurrencies());
    }

    public IdDTO createEventPromotionTemplate(CreateEventPromotionTemplateDTO createPromotionDTO) {
        securityManager.checkEntityAccessible(createPromotionDTO.getEntityId());
        if (isNull(createPromotionDTO.getType())) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.EVENT_PROMOTION_TYPE_MANDATORY);
        }
        CreatePromotion createPromotion = EventPromotionTemplateConverter.toMsPromotions(createPromotionDTO);
        createPromotion.setEntityId(createPromotionDTO.getEntityId());
        return entityPromotionsRepository.createEventPromotionTemplate(createPromotion);
    }

    public void deleteEventPromotionTemplate(Long promotionTemplateId) {
        getAndCheckEventPromotionTemplate(promotionTemplateId);
        entityPromotionsRepository.deleteEventPromotionTemplate(promotionTemplateId);
    }

    public void updateEventPromotionTemplate(Long promotionTemplateId, UpdateEventPromotionTemplateDTO body) {
        getAndCheckEventPromotionTemplate(promotionTemplateId);
        UpdateEventPromotionDetail requestBody = EventPromotionTemplateConverter.toMsDto(body);
        if(body.getDiscount() != null && body.getDiscount().getCurrencyCode() != null) {
            requestBody.getDiscount().setCurrencyId(validateCurrency(body.getDiscount().getCurrencyCode()));
        }
        this.entityPromotionsRepository.updateEventPromotionTemplate(promotionTemplateId, requestBody);
    }

    public PromotionChannelsDTO getEventPromotionTemplateChannels(Long promotionTemplateId) {
        getAndCheckEventPromotionTemplate(promotionTemplateId);
        EventPromotionChannels scopes = entityPromotionsRepository.getEventPromotionTemplateChannels(promotionTemplateId);
        return EventPromotionScopeConverter.toChannelsApi(scopes);
    }

    public void updateEventPromotionTemplateChannels(Long promotionTemplateId,
                                             UpdatePromotionChannelsDTO body) {
        entitiesRepository.getEntity(getAndCheckEventPromotionTemplate(promotionTemplateId).getEntityId());
        UpdateEventPromotionChannels scopes = EventPromotionScopeConverter.toChannelsMs(body);
        this.entityPromotionsRepository.updateEventPromotionChannels(promotionTemplateId, scopes);
    }

    public ChannelContentTextListDTO<PromotionChannelContentTextType> getEventPromotionTemplateChannelContentTexts(Long promotionTemplateId,
                                                                                    PromotionChannelContentTextFilter filter) {
        getAndCheckEventPromotionTemplate(promotionTemplateId);
        CommunicationElementFilter<PromotionTagType> communicationElementFilter = ChannelContentConverter
                .fromPromotionFilter(filter, masterdataService);

        List<BaseCommunicationElement> comElements = entityPromotionsRepository.getEventPromotionTemplateChannelContentTexts(promotionTemplateId, communicationElementFilter);

        comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage));

        return ChannelContentConverter.fromMsPromotionText(comElements);
    }

    public void updateEventPromotionChannelContentTexts(Long promotionTemplateId,
                                                        PromotionChannelContentTextListDTO content) {
        Entity entity = entitiesRepository.getEntity(getAndCheckEventPromotionTemplate(promotionTemplateId).getEntityId());
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentTextDTO<PromotionChannelContentTextType> element : content.getTexts()) {
            PromotionChannelContentTextType type = element.getType();
            String value = element.getValue();
            validateLength(type, value);
            element.setLanguage(
                    ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }
        entityPromotionsRepository.updateEventPromotionTemplateChannelContentTexts(promotionTemplateId,
                ChannelContentConverter.toMsPromotionText(content.getTexts()));
    }

    private PromotionTemplateDetail getAndCheckEventPromotionTemplate(Long promotionTemplateId) {
        PromotionTemplateDetail promotionTemplateDetail = entityPromotionsRepository.getEventPromotionTemplateDetail(promotionTemplateId);
        if (isNull(promotionTemplateDetail)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EVENT_PROMOTION_TEMPLATE_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(promotionTemplateDetail.getEntityId());
        return promotionTemplateDetail;
    }

    private static void validateLength(PromotionChannelContentTextType type, String value) {
        if (PromotionChannelContentTextType.DESCRIPTION.equals(type) && value.length() > DESCRIPTION_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Description must have between 1 and 1000 characters");
        } else if (PromotionChannelContentTextType.NAME.equals(type) && value.length() > NAME_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Name must have between 1 and 40 characters");
        }
    }

    private Long validateCurrency(String currencyCode) {
       return masterdataService.getCurrencies().stream()
                .filter(c-> c.getCode().equals(currencyCode))
                .findFirst().orElseThrow(()-> new OneboxRestException(ApiMgmtErrorCode.ERROR_CURRENCY_DOESNT_EXIST)).getId();

    }
}
