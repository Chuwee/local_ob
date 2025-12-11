package es.onebox.mgmt.datasources.ms.promotion.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.promotion.MsPromotionDatasource;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateFilter;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityPromotionsRepository {

    private final MsPromotionDatasource msPromotionDatasource;

    @Autowired
    public EntityPromotionsRepository(MsPromotionDatasource msPromotionDatasource) {
        this.msPromotionDatasource = msPromotionDatasource;
    }

    public EventPromotionTemplates getEventPromotionTemplates(PromotionTemplateFilter filter) {
        return msPromotionDatasource.getEventPromotionTemplates(filter);
    }

    public PromotionTemplateDetail getEventPromotionTemplateDetail(Long promotionTemplateId) {
        return msPromotionDatasource.getEventPromotionTemplate(promotionTemplateId);
    }

    public IdDTO createEventPromotionTemplate(CreatePromotion createPromotion) {
        return msPromotionDatasource.createEventPromotionTemplate(createPromotion);
    }

    public void deleteEventPromotionTemplate(Long promotionTemplateId) {
        msPromotionDatasource.deleteEventPromotionTemplate(promotionTemplateId);
    }

    public void updateEventPromotionTemplate(Long promotionId, UpdateEventPromotionDetail body) {
        msPromotionDatasource.updateEventPromotionTemplate(promotionId, body);
    }

    public EventPromotionChannels getEventPromotionTemplateChannels(Long promotionTemplateId) {
        return msPromotionDatasource.getEventPromotionTemplateChannels(promotionTemplateId);
    }

    public void updateEventPromotionChannels(Long promotionId, UpdateEventPromotionChannels scopes) {
        msPromotionDatasource.updateEventPromotionTemplateChannels(promotionId, scopes);
    }

    public List<BaseCommunicationElement> getEventPromotionTemplateChannelContentTexts(Long promotionTemplateId, CommunicationElementFilter<PromotionTagType> filter) {
        ChannelContentsUtils.addPromotionTagsToFilter(filter);
        return msPromotionDatasource.getEventPromotionTemplateChannelContentTexts(promotionTemplateId, filter);
    }

    public void updateEventPromotionTemplateChannelContentTexts(Long promotionTemplateId, List<BaseCommunicationElement> elements) {
        msPromotionDatasource.updateEventPromotionTemplateCommunicationElements(promotionTemplateId, elements);
    }
}
