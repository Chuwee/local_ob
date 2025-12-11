package es.onebox.event.events.amqp.tiermodification;

import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dto.TierCreationRequestDTO;
import es.onebox.event.events.service.EventTierService;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class TierModificationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TierModificationProcessor.class);

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.now();
    private static final double DEFAULT_PRICE = 0.0;
    public static final String INVALID_VENUE_TEMPLATE_ID = "Message with invalid venue template id";
    public static final String INVALID_EVENT_ID = "Message with invalid event id";


    private final EventTierService eventTierService;
    private final TierDao tierDao;
    private final  VenueConfigDao venueConfigDao;
    private final PriceTypeConfigDao priceZoneConfigDao;

    @Autowired
    public TierModificationProcessor(EventTierService eventTierService,TierDao tierDao,VenueConfigDao venueConfigDao,
                                     PriceTypeConfigDao priceZoneConfigDao){
        this.eventTierService = eventTierService;
        this.tierDao = tierDao;
        this.venueConfigDao = venueConfigDao;
        this.priceZoneConfigDao = priceZoneConfigDao;
    }

    @Override
    public void execute(Exchange exchange) {
        TierModificationMessage message = exchange.getIn().getBody(TierModificationMessage.class);

        logInfo("Started to process message: " + message.toString());

        switch (message.getAction()) {
            case CREATE_DEFAULT_TIERS_FOR_VENUE_TEMPLATE:
                createDefaultTiersForTemplate(message.getVenueTemplateId());
                break;
            case DELETE_TIERS_FOR_VENUE_TEMPLATE:
                deleteTiersForVenueTemplate(message.getVenueTemplateId());
                break;
            case CREATE_DEFAULT_TIER_FOR_PRICE_TYPE:
                createDefaultTierForPriceType(message.getPriceTypeId(), message.getVenueTemplateId());
                break;
            case CREATE_DEFAULT_TIERS_FOR_EVENT:
                createDefaultTiersForEvent(message.getEventId());
                break;
            case DELETE_TIERS_FOR_EVENT:
                deleteTiersForEvent(message.getEventId());
                break;
            case EVALUATE_TIERS:
                evaluateTiers(message.getEventId(), message.getPriceTypeId());
                break;
            case INCREMENT_TIER_LIMIT_FOR_EVENT:
                incrementTierLimitForEvent(message.getTierId(), message.getEventId(),
                    message.getSaleGroupId());
                break;

            default:
                logError("Message with invalid action");
        }
    }

    private void incrementTierLimitForEvent(Long tierId, Long eventId, Long saleGroupId) {
        eventTierService.incrementTierLimitForEvent(tierId, eventId, saleGroupId);
    }

    private void createDefaultTiersForEvent(Long eventId) {
        if (eventId == null) {
            logError(INVALID_EVENT_ID);
            return;
        }
        List<CpanelZonaPreciosConfigRecord> priceTypes = priceZoneConfigDao.getPriceZoneByEventId(eventId);
        createDefaultTiers(eventId, priceTypes);
    }

    private void deleteTiersForEvent(Long eventId) {
        if (eventId == null) {
            logError(INVALID_EVENT_ID);
            return;
        }
        List<CpanelTierRecord> tiers = tierDao.getByEventId(eventId.intValue());
        deleteTiers(eventId, tiers);
    }

    private void createDefaultTiersForTemplate(Long venueTemplateId) {
        if (venueTemplateId == null) {
            logError(INVALID_VENUE_TEMPLATE_ID);
            return;
        }
        CpanelConfigRecintoRecord venueConfig = venueConfigDao.getById(venueTemplateId.intValue());
        List<ZonaPreciosConfigRecord> priceTypes = priceZoneConfigDao.getPriceZone(venueTemplateId, null);
        createDefaultTiersForTemplate(venueConfig.getIdevento().longValue(), priceTypes);
    }

    private void deleteTiersForVenueTemplate(Long venueTemplateId) {
        if (venueTemplateId == null) {
            logError(INVALID_VENUE_TEMPLATE_ID);
            return;
        }
        CpanelConfigRecintoRecord venueConfig = venueConfigDao.getById(venueTemplateId.intValue());
        if (venueConfig == null) {
            logError("Venue template not found for venue template id: " + venueTemplateId);
            return;
        }
        List<TierRecord> tiers = tierDao.findByVenueTemplate(venueTemplateId.intValue());
        deleteTiers(venueConfig.getIdevento().longValue(), tiers);
    }

    private void createDefaultTierForPriceType(Long priceTypeId, Long venueTemplateId) {
        if (priceTypeId == null) {
            logError("Message with invalid price type id");
            return;
        }
        if (venueTemplateId == null) {
            logError(INVALID_VENUE_TEMPLATE_ID);
            return;
        }
        CpanelConfigRecintoRecord venueConfig = venueConfigDao.getById(venueTemplateId.intValue());
        CpanelZonaPreciosConfigRecord priceType = priceZoneConfigDao.getById(priceTypeId.intValue());
        createDefaultTiers(venueConfig.getIdevento().longValue(), Collections.singletonList(priceType));
    }

    private void createDefaultTiers(Long eventId, List<CpanelZonaPreciosConfigRecord> priceTypes) {
        for (CpanelZonaPreciosConfigRecord priceType : priceTypes) {
            TierCreationRequestDTO dto = new TierCreationRequestDTO();
            dto.setName(priceType.getDescripcion());
            dto.setPrice(DEFAULT_PRICE);
            dto.setPriceTypeId(priceType.getIdzona().longValue());
            dto.setStartDate(DEFAULT_START_DATE);
            eventTierService.createEventTier(eventId, dto);
        }
    }
    private void createDefaultTiersForTemplate(Long eventId, List<ZonaPreciosConfigRecord> priceTypes) {
        for (ZonaPreciosConfigRecord priceType : priceTypes) {
            TierCreationRequestDTO dto = new TierCreationRequestDTO();
            dto.setName(priceType.getDescripcion());
            dto.setPrice(DEFAULT_PRICE);
            dto.setPriceTypeId(priceType.getIdzona().longValue());
            dto.setStartDate(DEFAULT_START_DATE);
            eventTierService.createEventTier(eventId, dto);
        }
    }



    private void deleteTiers(Long eventId, List<? extends CpanelTierRecord> tiers) {
        for (CpanelTierRecord tier : tiers) {
            eventTierService.deleteEventTier(eventId, tier.getIdtier().longValue());
        }
    }

    private void evaluateTiers(Long eventId, Long priceTypeId) {
        eventTierService.evaluateAndExecuteTier(eventId, priceTypeId.intValue());
    }

    private void logError(String message) {
        LOGGER.error("[TIER MODIFICATION] {}", message);
    }

    private void logInfo(String message) {
        LOGGER.info("[TIER MODIFICATION] {}", message);
    }
}
