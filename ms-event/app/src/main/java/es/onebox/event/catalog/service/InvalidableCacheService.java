package es.onebox.event.catalog.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.config.InvalidableCache;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.springframework.stereotype.Service;

@Service
public class InvalidableCacheService {

    private final CacheRepository cacheRepository;

    public InvalidableCacheService(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public void invalidateEventPromotionsCaches(Long eventId) {
        cacheRepository.remove(InvalidableCache.EVENT_PROMOTIONS, new Object[]{eventId});
    }
    
    public void invalidateSurchargesCaches(EventIndexationContext ctx) {
        
        if (ctx == null || !EventIndexationType.FULL.equals(ctx.getType())) {
            return;
        }

        invalidateEventSurchargesCaches(ctx.getEventId());

        for (CpanelCanalEventoRecord channelEvent : ctx.getChannelEvents()) {
            Integer channelId = channelEvent.getIdcanal();

            if (channelId != null) {
                invalidateChannelSurchargesCaches(channelId);
                invalidateChannelEventSurchargesCaches(channelEvent.getIdcanaleevento());
                ctx.getEventChannel(channelId).ifPresent(eventChannelForCatalogRecord ->
                        invalidateEventChannelSurchargesCaches(eventChannelForCatalogRecord.getIdeventocanal())
                );
            }
        }
    }

    private void invalidateEventSurchargesCaches(Long eventId) {
        if (eventId == null) {
            return;
        }

        cacheRepository.remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        cacheRepository.remove(InvalidableCache.EVENT_ENTITY_SURCHARGES, new Object[]{eventId});
        cacheRepository.remove(InvalidableCache.EVENT_PROMOTIONS_SURCHARGES, new Object[]{eventId});
        cacheRepository.remove(InvalidableCache.EVENT_INVITATIONS_SURCHARGES, new Object[]{eventId});
        cacheRepository.remove(InvalidableCache.EVENT_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        cacheRepository.remove(InvalidableCache.EVENT_ENTITY_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});

    }

    private void invalidateChannelEventSurchargesCaches(Integer channelEventId) {
        if (channelEventId == null) {
            return;
        }

        cacheRepository.remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId});
        cacheRepository.remove(InvalidableCache.CHANNEL_EVENT_PROMOTIONS_SURCHARGES, new Object[]{channelEventId});
    }

    private void invalidateChannelSurchargesCaches(Integer channelId) {
        if (channelId == null) {
            return;
        }

        cacheRepository.remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId});
        cacheRepository.remove(InvalidableCache.CHANNEL_INVITATIONS_SURCHARGES, new Object[]{channelId});
        cacheRepository.remove(InvalidableCache.CHANNEL_SECONDARY_MARKET_SURCHARGES, new Object[]{channelId});
    }


    private void invalidateEventChannelSurchargesCaches(Integer eventChannelId) {
        if (eventChannelId == null) {
            return;
        }

        cacheRepository.remove(InvalidableCache.EVENT_CHANNEL_SURCHARGES, new Object[]{eventChannelId});
        cacheRepository.remove(InvalidableCache.EVENT_CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{eventChannelId});
    }

}
