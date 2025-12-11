package es.onebox.event.seasontickets.amqp.session;

import es.onebox.event.common.amqp.streamprogress.ProgressService;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import es.onebox.event.sessions.service.SessionService;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateSeasonTicketSessionProcessorTest {

    @Mock
    private SeasonTicketService seasonTicketService;
    @Mock
    private SessionService sessionService;
    @Mock
    private VenueConfigDao venueConfigDao;
    @Mock
    private RateDao rateDao;
    @Mock
    private ProgressService progressService;

    @InjectMocks
    private CreateSeasonTicketSessionProcessor processor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void process_withExistingRates_ok() {
        when(venueConfigDao.getVenueConfigStatus(anyInt())).thenReturn(new VenueConfigStatusRecord(1));
        when(rateDao.getRatesByEventId(anyInt())).thenReturn(buildRates());
        when(sessionService.createSession(anyLong(), any())).thenReturn(null);
        when(seasonTicketService.isSeasonTicketSessionCreated(anyLong())).thenReturn(false);
        try {
            processor.process(buildExchange());
        } catch (Exception e) {
            fail("Processor throws exception when it shouldn't");
        }
        verify(venueConfigDao).getVenueConfigStatus(anyInt());
        verify(rateDao).getRatesByEventId(anyInt());
        verify(sessionService).createSession(anyLong(), any());
    }

    @Test
    public void process_withoutRates_ok() {
        when(venueConfigDao.getVenueConfigStatus(anyInt())).thenReturn(new VenueConfigStatusRecord(1));
        when(rateDao.getRatesByEventId(anyInt())).thenReturn(Collections.emptyList(), buildRates());
        when(rateDao.insert(any())).thenAnswer(i -> i.getArguments()[0]);
        when(sessionService.createSession(anyLong(), any())).thenReturn(null);
        when(seasonTicketService.isSeasonTicketSessionCreated(anyLong())).thenReturn(false);
        try {
            processor.process(buildExchange());
        } catch (Exception e) {
            fail("Processor throws exception when it shouldn't");
        }
        verify(venueConfigDao).getVenueConfigStatus(anyInt());
        verify(rateDao, times(2)).getRatesByEventId(anyInt());
        verify(rateDao).insert(any());
        verify(sessionService).createSession(anyLong(), any());
    }

    @Test
    public void process_withSessionPreviouslyCreated_ok() {
        when(venueConfigDao.getVenueConfigStatus(anyInt())).thenReturn(new VenueConfigStatusRecord(1));
        when(rateDao.getRatesByEventId(anyInt())).thenReturn(Collections.emptyList(), buildRates());
        when(rateDao.insert(any())).thenAnswer(i -> i.getArguments()[0]);
        when(sessionService.createSession(anyLong(), any())).thenReturn(null);
        when(seasonTicketService.isSeasonTicketSessionCreated(anyLong())).thenReturn(true);
        try {
            processor.process(buildExchange());
        } catch (Exception e) {
            fail("Processor throws exception when it shouldn't");
        }
        verify(venueConfigDao).getVenueConfigStatus(anyInt());
        verify(rateDao, times(0)).getRatesByEventId(anyInt());
        verify(rateDao, times(0)).insert(any());
        verify(sessionService, times(0)).createSession(anyLong(), any());
    }

    @Test
    public void process_venueConfigStillProcessing_throwsException() throws Exception {
        when(venueConfigDao.getVenueConfigStatus(anyInt())).thenReturn(new VenueConfigStatusRecord(2));
        Assertions.assertThrows(Exception.class, () ->
                processor.process(buildExchange()));
    }


    private Exchange buildExchange() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        CreateSeasonTicketSessionMessage message = new CreateSeasonTicketSessionMessage();
        message.setName("Name");
        message.setChargeTaxId(1L);
        message.setTaxId(1L);
        message.setSeasonTicketId(1L);
        message.setVenueConfigId(1L);
        exchange.getIn().setBody(message);
        exchange.getIn().setHeader(Exchange.REDELIVERY_COUNTER, 0L);
        return exchange;
    }

    private List<RateRecord> buildRates() {
        RateRecord rate = new RateRecord();
        rate.setIdTarifa(1);
        rate.setDefecto(1);
        return Collections.singletonList(rate);
    }

}
