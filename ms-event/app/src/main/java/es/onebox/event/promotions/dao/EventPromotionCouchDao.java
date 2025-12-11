/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.promotions.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.promotions.dao.couch.EventPromotionDocument;
import es.onebox.event.promotions.dto.EventPromotion;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ignasi
 */
@Repository
@CouchRepository(prefixKey = EventPromotionCouchDao.EVENT_PROMOTION, bucket = EventPromotionCouchDao.ONEBOX_OPERATIVE, scope = "catalog", collection = "event-promotion")
public class EventPromotionCouchDao extends AbstractCouchDao<EventPromotionDocument> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EVENT_PROMOTION = "eventPromotionTemplate";

    public List<EventPromotion> searchEventPromotions(List<Long> eventIds, List<Long> promotionIds){
        if (eventIds == null && (promotionIds == null || promotionIds.isEmpty())) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "At least one of eventId or promotionIds must be provided", null);
        }
        String query = null;
        Map<String, Object> params = new HashMap<>();
        if (promotionIds != null && !promotionIds.isEmpty()) {
            params.put("promotionIds", promotionIds);
            query = "SELECT eventPromotion.* FROM " + this.from() + " eventPromotion " +
                    "WHERE `eventPromotionTemplateId` IN $promotionIds";
        } else if(eventIds != null && !eventIds.isEmpty()) {
            params.put("eventIds", eventIds);
            query = "SELECT eventPromotion.* FROM " + this.from() + " eventPromotion " +
                    "WHERE `eventId` IN $eventIds";
        }
        return queryList(query, params, EventPromotion.class, false);
    }
}
