package es.onebox.event.events.dynamicpricing;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.dp.tables.records.OneboxTimeSlotTierAssignmentsRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DynamicPricingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicPricingService.class);

    private final DynamicPricingDao dynamicPricingDao;
    private final RateDao rateDao;
    private final SessionRateDao sessionRateDao;
    private final ActiveDynamicPricingDao activeDynamicPricingDao;
    private final EventDao eventDao;
    private final RefreshDataService refreshDataService;

    @Autowired
    public DynamicPricingService(DynamicPricingDao dynamicPricingDao, RateDao rateDao, SessionRateDao sessionRateDao, ActiveDynamicPricingDao activeDynamicPricingDao, EventDao eventDao, RefreshDataService refreshDataService) {
        this.dynamicPricingDao = dynamicPricingDao;
        this.rateDao = rateDao;
        this.sessionRateDao = sessionRateDao;
        this.activeDynamicPricingDao = activeDynamicPricingDao;
        this.eventDao = eventDao;
        this.refreshDataService = refreshDataService;
    }

    public List<OneboxTimeSlotTierAssignmentsRecord> getAllPrices() {
        return dynamicPricingDao.getAll();
    }

    public List<OneboxTimeSlotTierAssignmentsRecord> getLastExecution() {
        return dynamicPricingDao.getLastExecutionRecords();
    }

    public void updatePrices() {
        List<OneboxTimeSlotTierAssignmentsRecord> dynamicPrices = dynamicPricingDao.getLastExecutionRecords();
        List<Long> eventIds = getDistinctEventIds(dynamicPrices);

        Map<Long, Map> ratesByEvent = new HashMap<>();
        Map<Long, EventDynamicPricing> eventDynamicPricingMap = getEventDynamicPricingConfigs(eventIds);

        for (Object eventId : eventIds) {
            Long id = (Long) eventId;
            Map<Integer, CpanelTarifaRecord> ratesById = new HashMap<>();
            EventDynamicPricing eventDynamicPricing = activeDynamicPricingDao.get(String.valueOf(eventId));
            eventDynamicPricingMap.put(id, eventDynamicPricing);

            if (eventDynamicPricing != null && eventDynamicPricing.isEnabled()) {
                List<CpanelTarifaRecord> rates = rateDao.getEventRates(id.intValue());
                for (CpanelTarifaRecord rate : rates) {
                    if (!ratesById.containsKey(rate.getIdtarifa())) {
                        ratesById.put(rate.getIdtarifa(), rate);
                    }
                }
            }
            ratesByEvent.put(id, ratesById);
        }

        for (OneboxTimeSlotTierAssignmentsRecord price : dynamicPrices) {
            if (isDynamicPricingEnabledByEventId(price.getIdEvent(), eventDynamicPricingMap)) {
                Integer rateId = findTier(ratesByEvent.get(price.getIdEvent()), price.getTier());
                Integer sessionId = price.getIdSession().intValue();

                if (rateId != null) {
                    sessionRateDao.cleanRatesForSessionId(sessionId);
                    sessionRateDao.createSessionRateRelationship(sessionId, rateId);

                    LOGGER.info("Updated session rate for session id {} with rate {}", sessionId, rateId);
                }
            }
        }

        refreshEvents(eventIds);
    }

    private boolean isDynamicPricingEnabledByEventId(Long eventId, Map<Long, EventDynamicPricing> eventDynamicPricingMap) {
        if (eventDynamicPricingMap != null && eventDynamicPricingMap.get(eventId) != null && eventDynamicPricingMap.get(eventId).isEnabled()) {
            return true;
        }
        return false;
    }

    private Map<Long, EventDynamicPricing> getEventDynamicPricingConfigs(List<Long> eventIds) {
        Map<Long, EventDynamicPricing> eventDynamicPricingMap = new HashMap<>();
        for (Long eventId : eventDynamicPricingMap.keySet()) {
            EventDynamicPricing eventDynamicPricing = activeDynamicPricingDao.get(String.valueOf(eventId));
            if (eventDynamicPricing != null) {
                eventDynamicPricingMap.put(eventId, eventDynamicPricing);
            }
        }
        return eventDynamicPricingMap;
    }

    private Integer findTier(Map<Integer, CpanelTarifaRecord> ratesById, String tier) {
        for (CpanelTarifaRecord tarifaRecord : ratesById.values()) {
            if (tarifaRecord.getDescripcion() != null && tarifaRecord.getDescripcion().equalsIgnoreCase(tier)) {
                return tarifaRecord.getIdtarifa();
            }
        }
        return null;
    }

    private List<Long> getDistinctEventIds(List<OneboxTimeSlotTierAssignmentsRecord> tiers) {
        return tiers.stream()
                .map(OneboxTimeSlotTierAssignmentsRecord::getIdEvent)
                .distinct()
                .collect(Collectors.toList());
    }
    private void refreshEvents(List<Long> eventIds) {
        eventIds.forEach(eventId -> {
            refreshDataService.refreshEvent(eventId, "postUpdate");
        });
    }

}
