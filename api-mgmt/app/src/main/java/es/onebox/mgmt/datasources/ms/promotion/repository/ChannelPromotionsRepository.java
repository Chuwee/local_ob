package es.onebox.mgmt.datasources.ms.promotion.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.promotion.MsPromotionDatasource;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.CreateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChannelPromotionsRepository {

    private final MsPromotionDatasource msPromotionDatasource;

    @Autowired
    public ChannelPromotionsRepository(MsPromotionDatasource msPromotionDatasource) {
        this.msPromotionDatasource = msPromotionDatasource;
    }

    public ChannelPromotions getChannelPromotions(Long channelId, ChannelPromotionsFilter filter) {
        return msPromotionDatasource.getChannelPromotions(channelId, filter);
    }

    public ChannelPromotionDetail getChannelPromotion(Long channelId, Long promotionId) {
        return msPromotionDatasource.getChannelPromotion(channelId, promotionId);
    }

    public IdDTO createChannelPromotion(Long channelId, CreateChannelPromotion request) {
        return msPromotionDatasource.createChannelPromotion(channelId, request);
    }

    public IdDTO cloneChannelPromotion(Long channelId, CreateChannelPromotion request) {
        return msPromotionDatasource.cloneChannelPromotion(channelId, request);
    }

    public void updateChannelPromotion(Long channelId, Long promotionId, UpdateChannelPromotion request) {
        msPromotionDatasource.updateChannelPromotion(channelId, promotionId, request);
    }

    public void deleteChannelPromotion(Long channelId, Long promotionId) {
        msPromotionDatasource.deleteChannelPromotion(channelId, promotionId);
    }

    public ChannelPromotionEvents getChannelPromotionEvents(Long channelId, Long promotionId) {
        return msPromotionDatasource.getChannelPromotionEvents(channelId, promotionId);
    }

    public void updateChannelPromotionEvents(Long channelId, Long promotionId, UpdateChannelPromotionEvents scopes) {
        msPromotionDatasource.updateChannelPromotionEvents(channelId, promotionId, scopes);
    }

    public ChannelPromotionSessions getChannelPromotionSessions(Long channelId, Long promotionId) {
        return msPromotionDatasource.getChannelPromotionSessions(channelId, promotionId);
    }

    public void updateChannelPromotionSessions(Long channelId, Long promotionId, UpdateChannelPromotionSessions scopes) {
        msPromotionDatasource.updateChannelPromotionSessions(channelId, promotionId, scopes);
    }

    public List<BaseCommunicationElement> getChannelContentTexts(Long channelId,
                                                                 Long promotionId, CommunicationElementFilter<PromotionTagType> filter) {
        ChannelContentsUtils.addPromotionTagsToFilter(filter);
        return msPromotionDatasource.getChannelCommunicationElements(channelId, promotionId, filter);
    }

    public void updateChannelPromotionContentTexts(Long channelId, Long promotionId, List<BaseCommunicationElement> elements) {
        msPromotionDatasource.updateChannelCommunicationElements(channelId, promotionId, elements);
    }

    public ChannelPromotionPriceTypes getChannelPromotionPriceTypes(Long channelId, Long promotionId) {
        return msPromotionDatasource.getChannelPromotionPriceTypes(channelId, promotionId);
    }

    public void updateChannelPromotionPriceTypes(Long channelId, Long promotionId, UpdateChannelPromotionPriceTypes body) {
        msPromotionDatasource.updateChannelPromotionPriceTypes(channelId, promotionId, body);
    }
}
