package es.onebox.event.catalog.service;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
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
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mmolinero on 28/02/19.
 */
public class Event2ESServiceTest {

    private static final Long EVENT_ID = 67L;

    @InjectMocks
    private Event2ESService event2ESService;

    @Mock
    private EventDao eventDao;
    @Mock
    private EventIndexationService eventIndexationService;
    @Mock
    private OccupationIndexationService occupationIndexationService;
    @Mock
    private EventPromotionsService eventPromotionsService;
    @Mock
    private EventDataIndexer eventDataIndexer;
    @Mock
    private SessionDataIndexer sessionDataIndexer;
    @Mock
    private ChannelEventDataIndexer channelEventDataIndexer;
    @Mock
    private ChannelSessionDataIndexer channelSessionDataIndexer;
    @Mock
    private VenueDescriptorIndexer venueDescriptorIndexer;
    @Mock
    private ChannelSessionAgencyDataIndexer channelSessionAgencyDataIndexer;
    @Mock
    private ChannelEventAgencyDataIndexer channelEventAgencyDataIndexer;
    @Mock
    private SeasonTicketDataIndexer seasonTicketDataIndexer;
    @Mock
    private EventElasticDao eventElasticDao;
    @Mock
    private InvalidableCacheService invalidableCacheService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void removeEvent() {
        CpanelEventoRecord eventRecord = getEventRecord(EventStatus.DELETED);
        when(eventDao.getById(Event2ESServiceTest.EVENT_ID.intValue())).thenReturn(eventRecord);

        event2ESService.updateCatalog(Event2ESServiceTest.EVENT_ID, null, EventIndexationType.FULL);

        verify(eventIndexationService, Mockito.times(1)).deleteEvent(Event2ESServiceTest.EVENT_ID);
    }

    @Test
    public void updateCatalog() {
        CpanelEventoRecord eventRecord = getEventRecord(EventStatus.READY);
        EventIndexationContext ctx = mock(EventIndexationContext.class);
        when(ctx.getEventId()).thenReturn(EVENT_ID);

        when(eventDao.getById(EVENT_ID.intValue())).thenReturn(eventRecord);
        when(eventIndexationService.prepareEventContext(eventRecord, null, EventIndexationType.FULL)).thenReturn(ctx);

        event2ESService.updateCatalog(EVENT_ID, null, EventIndexationType.FULL);

        verify(eventIndexationService, Mockito.times(1)).prepareEventContext(eventRecord, null, EventIndexationType.FULL);
        verify(eventDataIndexer, Mockito.times(1)).indexEvent(any());
        verify(sessionDataIndexer, Mockito.times(1)).indexSessions(any());
        verify(channelSessionDataIndexer, Mockito.times(1)).indexChannelSessions(any());
        verify(channelEventDataIndexer, Mockito.times(1)).indexChannelEvents(any());
        verify(venueDescriptorIndexer, Mockito.times(1)).indexVenueDescriptors(any());
        verify(channelSessionAgencyDataIndexer, Mockito.times(1)).indexChannelAgencySessions(any());
        verify(channelEventAgencyDataIndexer, Mockito.times(1)).indexChannelAgencyEvents(any());
        verify(seasonTicketDataIndexer, Mockito.times(1)).indexSeasonTicket(any());
    }

    @Test
    public void updateOccupation() {
        CpanelEventoRecord eventRecord = getEventRecord(EventStatus.READY);
        OccupationIndexationContext ctx = mock(OccupationIndexationContext.class);
        when(ctx.getEventId()).thenReturn(EVENT_ID);

        when(eventDao.getById(EVENT_ID.intValue())).thenReturn(eventRecord);
        when(occupationIndexationService.prepareOccupationContext(EVENT_ID, null)).thenReturn(ctx);

        event2ESService.updateOccupation(EVENT_ID, null);

        verify(occupationIndexationService, Mockito.times(1)).prepareOccupationContext(eq(EVENT_ID), isNull());
        verify(eventDataIndexer, Mockito.times(0)).indexEvent(any());
        verify(sessionDataIndexer, Mockito.times(0)).indexSessions(any());
        verify(venueDescriptorIndexer, Mockito.times(0)).indexVenueDescriptors(any());
        verify(channelSessionDataIndexer, Mockito.times(1)).indexOccupation(any());
        verify(channelEventDataIndexer, Mockito.times(1)).indexOccupation(any());
        verify(channelSessionAgencyDataIndexer, Mockito.times(1)).indexChannelAgencyOccupation(any());
        verify(channelEventAgencyDataIndexer, Mockito.times(1)).indexOccupation(any());
        verify(seasonTicketDataIndexer, Mockito.times(0)).indexSeasonTicket(any());
    }

    private CpanelEventoRecord getEventRecord(EventStatus eventStatus) {
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();

        cpanelEventoRecord.setIdevento(EVENT_ID.intValue());
        cpanelEventoRecord.setEstado(eventStatus.getId());

        return cpanelEventoRecord;
    }
}
