package es.onebox.event.catalog.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.config.InvalidableCache;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class InvalidabeCacheServiceTest {

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private InvalidableCacheService invalidableCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInvalidateEventPromotionsCaches() {
        Long eventId = 123L;

        invalidableCacheService.invalidateEventPromotionsCaches(eventId);

        verify(cacheRepository).remove(InvalidableCache.EVENT_PROMOTIONS, new Object[]{eventId});
    }

    @Test
    void testInvalidateSurchargesCaches_whenContextIsNull() {
        invalidableCacheService.invalidateSurchargesCaches(null);

        verifyNoInteractions(cacheRepository);
    }

    @Test
    void testInvalidateSurchargesCaches_whenTypeIsNotFull() {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(123);
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.PARTIAL_BASIC);

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verifyNoInteractions(cacheRepository);
    }

    @Test
    void testInvalidateSurchargesCaches_whenTypeIsFull_withValidEventId() {
        Long eventId = 123L;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);
        ctx.setChannelEvents(Collections.emptyList());

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_PROMOTIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_INVITATIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
    }

    @Test
    void testInvalidateSurchargesCaches_withChannelEvents_nullChannelId() {
        Long eventId = 123L;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(null);
        channelEvent.setIdcanaleevento(456);

        ctx.setChannelEvents(List.of(channelEvent));
        ctx.setEventChannels(Collections.emptyList());

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_PROMOTIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_INVITATIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository, never()).remove(eq(InvalidableCache.CHANNEL_EVENT_SURCHARGES), any());
        verify(cacheRepository, never()).remove(eq(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES), any());
    }

    @Test
    void testInvalidateSurchargesCaches_withChannelEvents_nullChannelEventId() {
        Long eventId = 123L;
        Integer channelId = 789;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(channelId);
        channelEvent.setIdcanaleevento(null);

        ctx.setChannelEvents(List.of(channelEvent));
        ctx.setEventChannels(Collections.emptyList());

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_INVITATIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_SECONDARY_MARKET_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository, never()).remove(eq(InvalidableCache.CHANNEL_EVENT_SURCHARGES), any());
        verify(cacheRepository, never()).remove(eq(InvalidableCache.CHANNEL_EVENT_PROMOTIONS_SURCHARGES), any());
    }

    @Test
    void testInvalidateSurchargesCaches_withChannelEvents_noEventChannel() {
        // Given
        Long eventId = 123L;
        Integer channelId = 789;
        Integer channelEventId = 456;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(channelId);
        channelEvent.setIdcanaleevento(channelEventId);

        ctx.setChannelEvents(List.of(channelEvent));
        ctx.setEventChannels(Collections.emptyList());

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_INVITATIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_SECONDARY_MARKET_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_PROMOTIONS_SURCHARGES, new Object[]{channelEventId});
        verify(cacheRepository, never()).remove(eq(InvalidableCache.EVENT_CHANNEL_SURCHARGES), any());
        verify(cacheRepository, never()).remove(eq(InvalidableCache.EVENT_CHANNEL_PROMOTIONS_SURCHARGES), any());
    }

    @Test
    void testInvalidateSurchargesCaches_withChannelEvents_withEventChannel() {
        // Given
        Long eventId = 123L;
        Integer channelId = 789;
        Integer channelEventId = 456;
        Integer eventChannelId = 999;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(channelId);
        channelEvent.setIdcanaleevento(channelEventId);

        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdcanal(channelId);
        eventChannel.setIdeventocanal(eventChannelId);

        ctx.setChannelEvents(List.of(channelEvent));
        ctx.setEventChannels(List.of(eventChannel));

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_PROMOTIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_INVITATIONS_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_ENTITY_SECONDARY_MARKET_SURCHARGES, new Object[]{eventId});
        
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_INVITATIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_SECONDARY_MARKET_SURCHARGES, new Object[]{channelId});
        
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_PROMOTIONS_SURCHARGES, new Object[]{channelEventId});
        
        verify(cacheRepository).remove(InvalidableCache.EVENT_CHANNEL_SURCHARGES, new Object[]{eventChannelId});
        verify(cacheRepository).remove(InvalidableCache.EVENT_CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{eventChannelId});
    }

    @Test
    void testInvalidateSurchargesCaches_withChannelEvents_withEventChannelNullId() {
        Long eventId = 123L;
        Integer channelId = 789;
        Integer channelEventId = 456;
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(channelId);
        channelEvent.setIdcanaleevento(channelEventId);

        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdcanal(channelId);
        eventChannel.setIdeventocanal(null);

        ctx.setChannelEvents(List.of(channelEvent));
        ctx.setEventChannels(List.of(eventChannel));

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_PROMOTIONS_SURCHARGES, new Object[]{channelEventId});
        verify(cacheRepository, never()).remove(eq(InvalidableCache.EVENT_CHANNEL_SURCHARGES), any());
        verify(cacheRepository, never()).remove(eq(InvalidableCache.EVENT_CHANNEL_PROMOTIONS_SURCHARGES), any());
    }

    @Test
    void testInvalidateSurchargesCaches_withMultipleChannelEvents() {
        // Given
        Long eventId = 123L;
        Integer channelId1 = 789;
        Integer channelEventId1 = 456;
        Integer eventChannelId1 = 999;
        Integer channelId2 = 888;
        Integer channelEventId2 = 555;
        
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setIdevento(eventId.intValue());
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, EventIndexationType.FULL);

        CpanelCanalEventoRecord channelEvent1 = new CpanelCanalEventoRecord();
        channelEvent1.setIdcanal(channelId1);
        channelEvent1.setIdcanaleevento(channelEventId1);

        CpanelCanalEventoRecord channelEvent2 = new CpanelCanalEventoRecord();
        channelEvent2.setIdcanal(channelId2);
        channelEvent2.setIdcanaleevento(channelEventId2);

        EventChannelForCatalogRecord eventChannel1 = new EventChannelForCatalogRecord();
        eventChannel1.setIdcanal(channelId1);
        eventChannel1.setIdeventocanal(eventChannelId1);

        ctx.setChannelEvents(List.of(channelEvent1, channelEvent2));
        ctx.setEventChannels(List.of(eventChannel1));

        invalidableCacheService.invalidateSurchargesCaches(ctx);

        verify(cacheRepository).remove(InvalidableCache.EVENT_SURCHARGES, new Object[]{eventId});
        
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId1});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId1});
        verify(cacheRepository).remove(InvalidableCache.EVENT_CHANNEL_SURCHARGES, new Object[]{eventChannelId1});
        
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_PROMOTIONS_SURCHARGES, new Object[]{channelId2});
        verify(cacheRepository).remove(InvalidableCache.CHANNEL_EVENT_SURCHARGES, new Object[]{channelEventId2});
    }
}
