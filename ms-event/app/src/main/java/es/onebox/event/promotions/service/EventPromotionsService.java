package es.onebox.event.promotions.service;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.InvalidableCache;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.record.EventPromotionConditionRateRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PromotionCommElemRecord;
import es.onebox.event.promotions.converter.EventPromotionConverter;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.promotions.dao.PromotionTemplateDao;
import es.onebox.event.promotions.dao.couch.EventPromotionDocument;
import es.onebox.event.promotions.dao.couch.PromotionEventCounterCouchDao;
import es.onebox.event.promotions.dao.couch.PromotionSessionCounterCouchDao;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.enums.PromotionStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventPromotionsService {

    private final EventPromotionTemplateDao eventPromotionTemplateDao;
    private final EventPromotionCouchDao eventPromotionCouchDao;
    private final PromotionTemplateDao promotionTemplateDao;
    private final PromotionEventCounterCouchDao eventCounterDao;
    private final PromotionSessionCounterCouchDao sessionCounterDao;
    private final RateDao rateDao;

    @Autowired
    public EventPromotionsService(EventPromotionTemplateDao eventPromotionTemplateDao,
                                  EventPromotionCouchDao eventPromotionCouchDao, PromotionTemplateDao promotionTemplateDao,
                                  PromotionEventCounterCouchDao eventCounterDao, PromotionSessionCounterCouchDao sessionCounterDao, RateDao rateDao) {
        this.eventPromotionTemplateDao = eventPromotionTemplateDao;
        this.eventPromotionCouchDao = eventPromotionCouchDao;
        this.promotionTemplateDao = promotionTemplateDao;
        this.eventCounterDao = eventCounterDao;
        this.sessionCounterDao = sessionCounterDao;
        this.rateDao = rateDao;
    }

    public EventPromotion getEventPromotionById(Long id) {
        var eventPromotion = eventPromotionCouchDao.get(String.valueOf(id));
        if (eventPromotion == null) {
            throw OneboxRestException.builder(MsEventErrorCode.PROMOTION_NOT_FOUND).build();
        }
        return eventPromotion;
    }

    public List<EventPromotion> searchEventPromotions(List<Long> eventIds, List<Long> promotionIds){
        return this.eventPromotionCouchDao.searchEventPromotions(eventIds, promotionIds);
    }

    public List<EventPromotionRecord> getEventPromotionRecords(Long eventId) {
        return this.eventPromotionTemplateDao.getPromotionsByEventId(eventId.intValue());
    }

    @Cached(key = InvalidableCache.EVENT_PROMOTIONS, expires = InvalidableCache.TTL)
    public List<EventPromotion> getCachedEventPromotions(@CachedArg Long eventId) {
        List<EventPromotionRecord> eventPromotions = this.eventPromotionTemplateDao.getPromotionsByEventId(eventId.intValue());
        if (CollectionUtils.isNotEmpty(eventPromotions)) {
            eventPromotions = eventPromotions.stream().filter(promotion -> BooleanUtils.isNotTrue(promotion.getUseEntityPacks())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(eventPromotions)) {
                return buildEventPromotions(eventId, eventPromotions);
            }
        }
        return Collections.emptyList();
    }

    public List<EventPromotion> storePromotionsInCouchbase(Long eventId, List<EventPromotionRecord> eventPromotions) {
        List<EventPromotionRecord> recordsToUpdate = new ArrayList<>(eventPromotions);
        List<EventPromotion> eventPromotionsCouch = eventPromotionCouchDao.searchEventPromotions(Collections.singletonList(eventId), null);
        if (CollectionUtils.isNotEmpty(eventPromotionsCouch)) {
            List<Long> eventPromotionsToAddIds = eventPromotionsCouch.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getActive() && !PromotionStatus.DELETED.equals(p.getStatus())))
                    .map(EventPromotion::getEventPromotionTemplateId)
                    .filter(id -> eventPromotions.stream().noneMatch(ep -> id.equals(ep.getEventPromotionTemplateId().longValue())))
                    .collect(Collectors.toList());
            List<EventPromotionRecord> recordsToAdd = getPromotionsByEventPromotionTemplateId(eventPromotionsToAddIds);
            recordsToUpdate.addAll(recordsToAdd);
        }

        if (CollectionUtils.isNotEmpty(recordsToUpdate)) {
            List<EventPromotion> promotions = buildEventPromotions(eventId, recordsToUpdate);
            if (CollectionUtils.isNotEmpty(promotions)) {
                for (EventPromotion p : promotions) {
                    EventPromotionDocument doc = new EventPromotionDocument();
                    BeanUtils.copyProperties(p, doc);
                    eventPromotionCouchDao.upsert(p.getEventPromotionTemplateId().toString(), doc);
                }
            }
            return promotions;
        }
        return Collections.emptyList();
    }

    private List<EventPromotionRecord> getPromotionsByEventPromotionTemplateId(List<Long> eventPromotionsToAddIds) {
        List<EventPromotionRecord> eventPromotions = eventPromotionTemplateDao.getPromotionsByEventPromotionTemplateId(eventPromotionsToAddIds);
        if (CollectionUtils.isNotEmpty(eventPromotions)) {
           eventPromotions = eventPromotions.stream().filter( promotion -> BooleanUtils.isNotTrue(promotion.getUseEntityPacks())).collect(Collectors.toList());
        }
        return eventPromotions;
    }

    public Long getCurrentPromotionUsageBySessionId(Long promotionId, Long sessionId) {
        return sessionCounterDao.get(promotionId.intValue(), sessionId.intValue());
    }

    public Long getCurrentPromotionUsageByEventId(Long promotionId, Long eventId) {
        return eventCounterDao.get(promotionId.intValue(), eventId.intValue());
    }

    private List<EventPromotion> buildEventPromotions(Long eventId, List<EventPromotionRecord> eventPromotions) {
        this.searchAndFillSessions(eventPromotions);
        this.searchAndFillCommunicationElements(eventPromotions);
        List<RateRecord> rates = rateDao.getRatesByEventId(eventId.intValue());
        return EventPromotionConverter.convert(eventPromotions, this.searchAndFillUsageConditions(eventPromotions), rates);
    }

    private void searchAndFillSessions(List<EventPromotionRecord> eventPromotions) {
        List<Integer> eventPromotionIds = eventPromotions.stream()
                .map(EventPromotionRecord::getEventPromotionTemplateId).collect(Collectors.toList());
        Map<Integer, List<Long>> result = this.eventPromotionTemplateDao.getPromotedSessionsByPromotionEventIds(eventPromotionIds);
        if (MapUtils.isNotEmpty(result)) {
            eventPromotions.forEach(record -> record.setSessions(result.remove(record.getEventPromotionTemplateId())));
        }
    }

    private void searchAndFillCommunicationElements(List<EventPromotionRecord> eventPromotions) {
        List<Integer> promotionTemplateIds = eventPromotions.stream()
                .map(EventPromotionRecord::getPromotionTemplateId).collect(Collectors.toList());
        Map<Integer, List<PromotionCommElemRecord>> result = this.promotionTemplateDao.getCommunicationElementsByPromotionTemplateIds(promotionTemplateIds);
        if (MapUtils.isNotEmpty(result)) {
            eventPromotions.forEach(record -> record.setCommElemRecords(result.remove(record.getPromotionTemplateId())));
        }
    }

    private List<EventPromotionConditionRateRecord> searchAndFillUsageConditions(List<EventPromotionRecord> eventPromotions) {
        List<Integer> promotionTemplateIds = eventPromotions.stream()
                .map(EventPromotionRecord::getEventPromotionTemplateId)
                .collect(Collectors.toList());
        return eventPromotionTemplateDao.getPromotionConditionRate(promotionTemplateIds);
    }
}
