package es.onebox.mgmt.datasources.ms.promotion.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.promotions.dto.PromotionsFilter;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.promotion.MsPromotionDatasource;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.ResetDiscountEventPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.EventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.UpdateEventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventPromotionsRepository {

    private final MsPromotionDatasource msPromotionDatasource;

    @Autowired
    public EventPromotionsRepository(MsPromotionDatasource msPromotionDatasource) {
        this.msPromotionDatasource = msPromotionDatasource;
    }

    public PromotionTemplates getEventPromotions(Long eventId, PromotionsFilter filter) {
        return msPromotionDatasource.getEventPromotions(eventId, filter);
    }

    public PromotionDetail getEventPromotion(Long eventId, Long eventPromotionId) {
        return msPromotionDatasource.getEventPromotion(eventId, eventPromotionId);
    }

    public void updateEventPromotion(Long eventId, Long eventPromotionId, UpdateEventPromotionDetail body) {
        msPromotionDatasource.updateEventPromotion(eventId, eventPromotionId, body);
    }

    public EventPromotionChannels getEventPromotionsChannels(Long eventId, Long eventPromotionId) {
		return msPromotionDatasource.getEventPromotionChannels(eventId, eventPromotionId);
	}

	public void updateEventPromotionChannels(Long eventId, Long eventPromotionId, UpdateEventPromotionChannels scopes) {
		msPromotionDatasource.updateEventPromotionChannels(eventId, eventPromotionId, scopes);
	}

    public EventPromotionSessions getEventPromotionsSessions(Long eventId, Long eventPromotionId) {
        return msPromotionDatasource.getEventPromotionSessions(eventId, eventPromotionId);
    }

    public void updateEventPromotionSessions(Long eventId, Long eventPromotionId, UpdateEventPromotionSessions scopes) {
        msPromotionDatasource.updateEventPromotionSessions(eventId, eventPromotionId, scopes);
    }

    public EventPromotionPriceTypes getEventPromotionsPriceTypes(Long eventId, Long eventPromotionId) {
        return msPromotionDatasource.getEventPromotionPriceTypes(eventId, eventPromotionId);
    }

    public void updateEventPromotionPriceTypes(Long eventId, Long eventPromotionId,
            UpdateEventPromotionPriceTypes scopes) {
        msPromotionDatasource.updateEventPromotionPriceTypes(eventId, eventPromotionId, scopes);
    }

    public EventPromotionRates getEventPromotionsRates(Long eventId, Long eventPromotionId) {
        return msPromotionDatasource.getEventPromotionRates(eventId, eventPromotionId);
    }

    public void updateEventPromotionRates(Long eventId, Long eventPromotionId, UpdateEventPromotionRates scopes) {
        msPromotionDatasource.updateEventPromotionRates(eventId, eventPromotionId, scopes);
    }

    public EventPromotionPacks getEventPromotionsPacks(Long eventId, Long eventPromotionId) {
        return msPromotionDatasource.getEventPromotionPacks(eventId, eventPromotionId);
    }

    public void updateEventPromotionPacks(Long eventId, Long eventPromotionId, UpdateEventPromotionPacks scopes) {
        msPromotionDatasource.updateEventPromotionPacks(eventId, eventPromotionId, scopes);
    }

    public IdDTO createEventPromotion(Long eventId, CreatePromotion createPromotion) {
        return msPromotionDatasource.createEventPromotion(eventId, createPromotion);
    }

    public List<IdDTO> cloneEventPromotion(Long eventId, List<ClonePromotion> clonePromotion) {
        return msPromotionDatasource.cloneEventPromotion(eventId, clonePromotion);
    }

    public List<IdDTO> cloneEventPromotionFromEntityTemplate(Long eventId, List<ClonePromotion> clonePromotion) {
        return msPromotionDatasource.cloneEntityPromotion(eventId, clonePromotion);
    }

    public void cloneEventPromotionsFromEntityTemplates(Long eventId, List<ClonePromotion> clonePromotion) {
        msPromotionDatasource.cloneEntityPromotions(eventId, clonePromotion);
    }

	public List<BaseCommunicationElement> getChannelContentTexts(Long eventId,
			Long promotionId, CommunicationElementFilter<PromotionTagType> filter) {
        ChannelContentsUtils.addPromotionTagsToFilter(filter);
        return msPromotionDatasource.getEventCommunicationElements(eventId, promotionId, filter);
    }

    public void updateChannelContentTexts(Long eventId, Long promotionId, List<BaseCommunicationElement> elements) {
        msPromotionDatasource.updateEventCommunicationElements(eventId, promotionId, elements);
    }

    public void deleteEventPromotion(Long eventId, Long eventPromotionId) {
        msPromotionDatasource.deleteEventPromotion(eventId, eventPromotionId);
    }

    public void resetDiscountEventPromotions(Long eventId, Long currencyId) {
        msPromotionDatasource.resetDiscountEventPromotions(eventId, new ResetDiscountEventPromotions(currencyId));
    }
}
