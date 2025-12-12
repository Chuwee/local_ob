package es.onebox.common.datasources.ms.promotion.repository;

import es.onebox.common.datasources.ms.promotion.dto.EventPromotionPriceTypesDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionChannelsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionEventSessionsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDetailDTO;
import es.onebox.common.datasources.ms.promotion.MsPromotionDatasource;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionRatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PromotionChannelRepository {

    private final MsPromotionDatasource msPromotionDatasource;

    @Autowired
    public PromotionChannelRepository(MsPromotionDatasource msPromotionDatasource) {
        this.msPromotionDatasource = msPromotionDatasource;
    }

    public PromotionDetailDTO getEventPromotion(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.getEventPromotion(eventId, promotionId);
    }

    public PromotionChannelsDTO getEventPromotionChannel(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.getEventPromotionChannels(eventId, promotionId);
    }

    public PromotionEventSessionsDTO getEventPromotionSession(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.getEventPromotionSessions(eventId, promotionId);
    }

    public EventPromotionPriceTypesDTO getEventPromotionPriceTypes(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.getEventPromotionPriceTypes(eventId, promotionId);
    }

    public PromotionRatesDTO getEventPromotionRates(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.getEventPromotionRates(eventId, promotionId);
    }

    public void putEventPromotion(final Long eventId, final Long promotionId, final PromotionDetailDTO promotion) {
        msPromotionDatasource.putEventPromotion(eventId, promotionId, promotion);
    }

    public List<CommunicationElementDTO> getEventCommunicationElements(final Long eventId, final Long promotionId) {
        return msPromotionDatasource.findCommunicationElements(eventId, promotionId);
    }

    public void putEventCommunicationElements(
            final Long eventId,
            final Long promotionId,
            final List<CommunicationElementDTO> elements) {
        msPromotionDatasource.updateCommunicationElements(eventId, promotionId, elements);
    }
}
