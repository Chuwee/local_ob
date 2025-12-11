package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchFilter;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketStatus;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract sealed class IndexationService permits OccupationIndexationService, EventIndexationService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final Byte ZERO = 0;

    protected final ChannelSessionIndexationService channelSessionIndexationService;
    protected final ChannelAgencyIndexationService channelAgencyIndexationService;
    protected final EventElasticDao eventElasticDao;
    protected final EventPromotionsService eventPromotionsService;
    protected final CacheRepository localCacheRepository;
    protected final EntitiesRepository entitiesRepository;
    protected final ChannelDao channelDao;
    protected final ChannelEventDao channelEventDao;
    protected final TicketsRepository ticketsRepository;
    protected final SessionDao sessionDao;
    protected final SessionTaxesDao sessionTaxesDao;
    protected final SalesGroupAssignmentDao salesGroupAssignmentDao;

    protected IndexationService(ChannelSessionIndexationService channelSessionIndexationService,
                                ChannelAgencyIndexationService channelAgencyIndexationService,
                                EventElasticDao eventElasticDao,
                                EventPromotionsService eventPromotionsService,
                                CacheRepository localCacheRepository,
                                EntitiesRepository entitiesRepository,
                                ChannelDao channelDao,
                                ChannelEventDao channelEventDao,
                                TicketsRepository ticketsRepository,
                                SessionDao sessionDao, SessionTaxesDao sessionTaxesDao,
                                SalesGroupAssignmentDao salesGroupAssignmentDao) {
        this.channelSessionIndexationService = channelSessionIndexationService;
        this.channelAgencyIndexationService = channelAgencyIndexationService;
        this.eventElasticDao = eventElasticDao;
        this.eventPromotionsService = eventPromotionsService;
        this.localCacheRepository = localCacheRepository;
        this.entitiesRepository = entitiesRepository;
        this.channelDao = channelDao;
        this.channelEventDao = channelEventDao;
        this.ticketsRepository = ticketsRepository;
        this.sessionDao = sessionDao;
        this.sessionTaxesDao = sessionTaxesDao;
        this.salesGroupAssignmentDao = salesGroupAssignmentDao;
    }


    protected Map<Long, List<Long>> getEventQuotas(List<CpanelCanalEventoRecord> channelEvents) {
        return channelEvents.stream()
                .filter(channelEvent -> ZERO.equals(channelEvent.getTodosgruposventa()))
                .collect(Collectors.toMap(
                        channelEvent -> channelEvent.getIdcanal().longValue(),
                        channelEvent -> salesGroupAssignmentDao.getChannelEventQuotaIds(channelEvent.getIdcanaleevento())));
    }

    protected <T extends BaseIndexationContext<?, ?>> void prepareSecondaryLocationsForSale(T ctx, EntityDTO entity, Long sessionId) {
        if (entity != null && BooleanUtils.isTrue(entity.getUseSecondaryMarket())) {
            SecondaryMarketSearchFilter filter = buildSecondaryMarketSessionFilter(ctx, sessionId);
            try {
                List<SecondaryMarketSearch> response = ticketsRepository.getSecondaryMarketLocations(filter);
                if (response != null) {
                    ctx.setSecondaryMarketForSale(response);
                }
            } catch (Exception e) {
                LOGGER.error("[CATALOG MIGRATION] - Error on secondary market location search for event {} - {}", ctx.getEventId(), e.getMessage());
            }
        }
    }

    private <T extends BaseIndexationContext<?, ?>> SecondaryMarketSearchFilter buildSecondaryMarketSessionFilter(T ctx, Long sessionId) {
        SecondaryMarketSearchFilter filter = new SecondaryMarketSearchFilter();
        if (sessionId != null) {
            filter.setSessionId(sessionId);
        } else {
            List<Long> sessionIds;
            if (ctx instanceof EventIndexationContext ec) {
                sessionIds = ec.getSessions().stream().map(CpanelSesionRecord::getIdsesion).map(Long::valueOf).collect(Collectors.toList());
            } else {
                List<SessionRecord> sessions = sessionDao.findActiveSessionsByEventId(ctx.getEventId().intValue());
                sessionIds = sessions.stream().map(SessionRecord::getIdsesion).map(Long::valueOf).collect(Collectors.toList());
            }
            filter.setSessionIds(sessionIds);
        }
        filter.setStatus(List.of(SecondaryMarketStatus.FOR_SALE));
        return filter;
    }
}
