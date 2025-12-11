package es.onebox.event.catalog.service;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelEventAgencyDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelEventDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelSessionAgencyDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelSessionDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.EventDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.SeasonTicketDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.SessionDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.VenueDescriptorIndexer;
import es.onebox.event.catalog.elasticsearch.service.EventIndexationService;
import es.onebox.event.catalog.elasticsearch.service.OccupationIndexationService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.exception.EventIndexationFullReload;
import es.onebox.jooq.annotation.MySQLReadReplica;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Event2ESService {

    private final EventIndexationService eventIndexationService;
    private final OccupationIndexationService occupationIndexationService;
    private final InvalidableCacheService invalidableCacheService;
    private final EventDataIndexer eventDataIndexer;
    private final SessionDataIndexer sessionDataIndexer;
    private final ChannelEventDataIndexer channelEventDataIndexer;
    private final ChannelSessionAgencyDataIndexer channelAgencySessionDataIndexer;
    private final ChannelEventAgencyDataIndexer channelAgencyEventDataIndexer;
    private final ChannelSessionDataIndexer channelSessionDataIndexer;
    private final VenueDescriptorIndexer venueDescriptorIndexer;
    private final EventDao eventDao;
    private final SeasonTicketDataIndexer seasonTicketIndexer;

    private static final Logger LOGGER = LoggerFactory.getLogger(Event2ESService.class);


    @Autowired
    public Event2ESService(EventIndexationService eventIndexationService, OccupationIndexationService occupationIndexationService,
                           EventDataIndexer eventDataIndexer,
                           SessionDataIndexer sessionDataIndexer,
                           ChannelEventDataIndexer channelEventDataIndexer,
                           ChannelSessionAgencyDataIndexer channelAgencyDataIndexer,
                           ChannelEventAgencyDataIndexer channelAgencyEventDataIndexer,
                           ChannelSessionDataIndexer channelSessionDataIndexer,
                           EventDao eventDao,
                           VenueDescriptorIndexer venueDescriptorIndexer,
                           SeasonTicketDataIndexer seasonTicketIndexer,
                           InvalidableCacheService invalidableCacheService) {
        this.eventIndexationService = eventIndexationService;
        this.occupationIndexationService = occupationIndexationService;
        this.eventDataIndexer = eventDataIndexer;
        this.sessionDataIndexer = sessionDataIndexer;
        this.channelEventDataIndexer = channelEventDataIndexer;
        this.channelAgencySessionDataIndexer = channelAgencyDataIndexer;
        this.channelAgencyEventDataIndexer = channelAgencyEventDataIndexer;
        this.channelSessionDataIndexer = channelSessionDataIndexer;
        this.eventDao = eventDao;
        this.venueDescriptorIndexer = venueDescriptorIndexer;
        this.seasonTicketIndexer = seasonTicketIndexer;
        this.invalidableCacheService = invalidableCacheService;
    }

    @MySQLReadReplica
    public Integer updateCatalog(Long eventId, Long sessionId, EventIndexationType indexationType) {

        invalidableCacheService.invalidateEventPromotionsCaches(eventId);
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());

        if (EventStatus.DELETED.getId().equals(event.getEstado())) {
            return eventIndexationService.deleteEvent(eventId).intValue();
        }

        EventIndexationContext ctx = null;
        try {
            ctx = eventIndexationService.prepareEventContext(event, sessionId, indexationType);
            indexEventData(ctx);

        } catch (EventIndexationFullReload e) {
            LOGGER.warn("[EVENT CATALOG] eventId: {} - Indexation {} failed, remigrate as FULL - detail: {}", eventId, indexationType, e.getMessage());
            //Reload context and migrate as FULL
            if (ctx != null) {
                eventIndexationService.prepareFullEventContext(event, sessionId, ctx);
                ctx.setType(EventIndexationType.FULL);
                indexEventData(ctx);
            }
        }

        return ctx != null ? ctx.getNumDocumentsIndexed() : 0;
    }

    @MySQLReadReplica
    public Integer updateOccupation(Long eventId, Long sessionId) {
        OccupationIndexationContext ctx = occupationIndexationService.prepareOccupationContext(eventId, sessionId);
        indexOccupation(ctx);

        return ctx != null ? ctx.getNumDocumentsIndexed() : 0;
    }

    private void indexEventData(EventIndexationContext ctx) {
        eventDataIndexer.indexEvent(ctx);
        venueDescriptorIndexer.indexVenueDescriptors(ctx);
        sessionDataIndexer.indexSessions(ctx);
        channelSessionDataIndexer.indexChannelSessions(ctx);
        channelEventDataIndexer.indexChannelEvents(ctx);
        channelAgencySessionDataIndexer.indexChannelAgencySessions(ctx);
        channelAgencyEventDataIndexer.indexChannelAgencyEvents(ctx);
        seasonTicketIndexer.indexSeasonTicket(ctx);
    }

    private void indexOccupation(OccupationIndexationContext ctx) {
        channelSessionDataIndexer.indexOccupation(ctx);
        channelEventDataIndexer.indexOccupation(ctx);
        channelAgencySessionDataIndexer.indexChannelAgencyOccupation(ctx);
        channelAgencyEventDataIndexer.indexOccupation(ctx);
    }

}
