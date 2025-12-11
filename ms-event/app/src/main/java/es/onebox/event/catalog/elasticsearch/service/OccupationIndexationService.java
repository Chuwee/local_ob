package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.utils.EventContextUtils;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public final class OccupationIndexationService extends IndexationService {

    private final EventDao eventDao;
    private final ChannelEventElasticDao channelEventElasticDao;
    private final ChannelEventAgencyElasticDao channelEventAgencyElasticDao;

    public OccupationIndexationService(
            ChannelSessionIndexationService channelSessionIndexationService,
            ChannelAgencyIndexationService channelAgencyIndexationService,
            EventElasticDao eventElasticDao,
            EventPromotionsService eventPromotionsService,
            CacheRepository localCacheRepository,
            EntitiesRepository entitiesRepository,
            ChannelDao channelDao,
            ChannelEventDao channelEventDao,
            TicketsRepository ticketsRepository,
            SessionDao sessionDao, SessionTaxesDao sessionTaxesDao, SalesGroupAssignmentDao salesGroupAssignmentDao,
            EventDao eventDao, ChannelEventElasticDao channelEventElasticDao, ChannelEventAgencyElasticDao channelEventAgencyElasticDao) {
        super(channelSessionIndexationService, channelAgencyIndexationService, eventElasticDao, eventPromotionsService,
                localCacheRepository, entitiesRepository, channelDao, channelEventDao, ticketsRepository, sessionDao, sessionTaxesDao, salesGroupAssignmentDao);
        this.eventDao = eventDao;
        this.channelEventElasticDao = channelEventElasticDao;
        this.channelEventAgencyElasticDao = channelEventAgencyElasticDao;
    }


    public OccupationIndexationContext prepareOccupationContext(Long eventId, Long sessionId) {
        CpanelEventoRecord eventRecord = getBasicEventRecord(eventId);
        OccupationIndexationContext ctx = new OccupationIndexationContext(eventRecord, sessionId);
        EventData eventData = eventElasticDao.get(eventId);
        if (eventData == null) {
            return null;
        }
        ctx.setEventData(eventData);
        List<ChannelEventData> channelEvents = channelEventElasticDao.getByEventId(eventId);
        ctx.setChannels(prepareChannelsCached(EventContextUtils.filterChannelsByChannelEvent(channelEvents)));
        ctx.setChannelEvents(channelEvents);
        ctx.setQuotasByChannel(getEventQuotas(eventId));
        ctx.setEventPromotions(eventPromotionsService.getCachedEventPromotions(eventId));
        EntityDTO eventEntity = localCacheRepository.cached(LocalCache.ENTITY_KEY, LocalCache.ENTITY_TTL, TimeUnit.SECONDS,
                () -> entitiesRepository.getEntity(ctx.getEventData().getEvent().getEntityId()), new Object[]{ctx.getEventData().getEvent().getEntityId()});
        prepareSecondaryLocationsForSale(ctx, eventEntity, sessionId);
        prepareChannelEventAgenciesInfo(ctx);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
        return ctx;
    }

    private CpanelEventoRecord getBasicEventRecord(Long eventId) {
        return localCacheRepository.cached(LocalCache.EVENT_TYPE_KEY, LocalCache.EVENT_TYPE_TTL, TimeUnit.SECONDS, () -> {
            CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
            CpanelEventoRecord response = new CpanelEventoRecord();
            response.setIdevento(eventRecord.getIdevento());
            response.setTipoevento(eventRecord.getTipoevento());
            response.setIdentidad(eventRecord.getIdentidad());
            return response;
        }, new Object[]{eventId});
    }

    private<T extends Number> Map<Long, ChannelInfo> prepareChannelsCached(List<T> channelIds) {
        return localCacheRepository.cached(LocalCache.CHANNELS_KEY, LocalCache.CHANNELS_TTL_EXTENDED, TimeUnit.SECONDS, () -> {
            List<ChannelInfo> channelInfo = channelDao.getByIds(channelIds);
            return channelInfo.stream().collect(Collectors.toMap(ChannelInfo::getId, Function.identity()));
        }, new Object[]{channelIds});
    }

    private Map<Long, List<Long>> getEventQuotas(Long eventId) {
        List<CpanelCanalEventoRecord> channelEvents = channelEventDao.getChannelEvents(eventId);
        return getEventQuotas(channelEvents);
    }

    private void prepareChannelEventAgenciesInfo(OccupationIndexationContext ctx) {
        Map<Long, List<Long>> channelAgencies = channelAgencyIndexationService.prepareB2BContext(ctx);
        ctx.setChannelsWithAgencies(channelAgencies);
        if (MapUtils.isNotEmpty(channelAgencies)) {
            List<ChannelEventAgencyData> channelEventAgencies = new ArrayList<>();
            channelAgencies.values().stream().flatMap(List::stream).distinct().forEach(agencyId -> {
                List<ChannelEventAgencyData> channelEvents = channelEventAgencyElasticDao.getByEventIdAndAgencyId(ctx.getEventId(), agencyId);
                if (CollectionUtils.isNotEmpty(channelEvents)) {
                    channelEventAgencies.addAll(channelEvents);
                }
            });
            ctx.setChannelEventAgencies(channelEventAgencies);
        }
    }
}
