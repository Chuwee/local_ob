package es.onebox.event.catalog;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.service.Event2ESService;
import es.onebox.event.catalog.service.PackRefreshService;
import es.onebox.event.catalog.utils.EventMigrationStatus;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

public class EventMigrationProcessorTest {

    public static final long EVENT_ID = 1;
    public static final long SESSION_ID = 1;
    @Mock
    private Event2ESService event2ESService;
    @Mock
    private CacheRepository cacheRepository;
    @Mock
    private PackRefreshService packRefreshService;

    private EventMigrationProcessor eventMigrationProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.eventMigrationProcessor = new EventMigrationProcessor(event2ESService, cacheRepository, packRefreshService);

        Mockito.when(event2ESService.updateCatalog(anyLong(), anyLong(), any())).thenReturn(1);
    }

    @Test
    public void testMigrationStatus() throws InterruptedException {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        EventMigrationMessage message = new EventMigrationMessage();
        message.setEventId(EVENT_ID);
        message.setSessionId(EVENT_ID);
        message.setEnqueueTime(ZonedDateTime.now().minusMinutes(1L).toString());
        exchange.getIn().setBody(message);

        Mockito.when(cacheRepository.get(any(), any())).thenReturn(null);

        eventMigrationProcessor.execute(exchange);

        Mockito.verify(event2ESService, Mockito.times(1)).updateCatalog(eq(EVENT_ID), eq(SESSION_ID), any());
        Mockito.verify(cacheRepository, Mockito.times(2)).set(any(), any(), anyInt(), any(), any());
    }

    @Test
    public void testMigrationStatus_discardByTime() throws InterruptedException {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        EventMigrationMessage message = new EventMigrationMessage();
        message.setEventId(EVENT_ID);
        message.setSessionId(EVENT_ID);
        message.setEnqueueTime(ZonedDateTime.now().minusMinutes(1L).toString());
        message.setRefreshType(EventIndexationType.FULL.name());
        exchange.getIn().setBody(message);

        boolean running = false;
        EventMigrationStatus eventMigrationStatus = new EventMigrationStatus(ZonedDateTime.now(), EventIndexationType.FULL, running);
        Mockito.when(cacheRepository.get(any(), any())).thenReturn(eventMigrationStatus);

        eventMigrationProcessor.execute(exchange);

        Mockito.verify(event2ESService, Mockito.times(0)).updateCatalog(eq(EVENT_ID), eq(SESSION_ID), any());
    }

    @Test
    public void testMigrationStatus_notDiscardByTime_differentIndexationType() throws InterruptedException {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        EventMigrationMessage message = new EventMigrationMessage();
        message.setEventId(EVENT_ID);
        message.setSessionId(EVENT_ID);
        message.setEnqueueTime(ZonedDateTime.now().minusMinutes(1L).toString());
        message.setRefreshType(EventIndexationType.PARTIAL_COM_ELEMENTS.name());
        exchange.getIn().setBody(message);

        boolean running = false;
        EventMigrationStatus eventMigrationStatus = new EventMigrationStatus(ZonedDateTime.now(), EventIndexationType.PARTIAL_BASIC, running);
        Mockito.when(cacheRepository.get(any(), any())).thenReturn(eventMigrationStatus);

        eventMigrationProcessor.execute(exchange);

        Mockito.verify(event2ESService, Mockito.times(1)).updateCatalog(eq(EVENT_ID), eq(SESSION_ID), any());
    }

    @Test
    public void testMigrationStatus_retryRunning() throws InterruptedException {
        try {
            Field field = EventMigrationProcessor.class.getDeclaredField("REPROCESS_TTL");
            field.setAccessible(true);
            field.set(null, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        EventMigrationMessage message = new EventMigrationMessage();
        message.setEventId(EVENT_ID);
        message.setSessionId(EVENT_ID);
        ZonedDateTime enqueueTime = ZonedDateTime.now().minusSeconds(2L);
        message.setEnqueueTime(enqueueTime.toString());
        exchange.getIn().setBody(message);

        boolean running = true;
        ZonedDateTime beginTime = ZonedDateTime.now().minusSeconds(3L);
        EventMigrationStatus firstMigrationStatus = new EventMigrationStatus(beginTime, EventIndexationType.PARTIAL_BASIC, running);

        running = false;
        EventMigrationStatus secondMigrationStatus = new EventMigrationStatus(beginTime, EventIndexationType.PARTIAL_BASIC, running);

        Mockito.when(cacheRepository.get(any(), any())).thenReturn(
                firstMigrationStatus, secondMigrationStatus);

        eventMigrationProcessor.execute(exchange);

        Mockito.verify(event2ESService, Mockito.times(1)).updateCatalog(eq(EVENT_ID), eq(SESSION_ID), any());
        Mockito.verify(cacheRepository, Mockito.times(2)).get(any(), any());
        Mockito.verify(cacheRepository, Mockito.times(2)).set(any(), any(), anyInt(), any(), any());
    }

}
