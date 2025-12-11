package es.onebox.event.archiver;

import es.onebox.event.events.amqp.sessionarchiver.SessionArchiverProducerService;
import es.onebox.event.events.archiver.EventArchiverProcessor;
import es.onebox.event.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.TriggerFiredBundle;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventArchiverProcessorTest {

    @Mock
    private EventService eventService;
    @Mock
    private SessionArchiverProducerService sessionArchiverProducerService;

    @InjectMocks
    private EventArchiverProcessor processor;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void processArchivation() throws JobExecutionException {

        Mockito.when(eventService.getSessionIdsToArchive(Mockito.any(ZonedDateTime.class), Mockito.anyInt())).thenReturn(new ArrayList<>(Arrays.asList(1L, 2L)));

        JobDetail detail = new JobDetailImpl();
        detail.getJobDataMap().put("shard", 1);
        TriggerFiredBundle trigger = new TriggerFiredBundle(detail, new CronTriggerImpl(), null, false,
                null, null, null, null);
        JobExecutionContextImpl context = new JobExecutionContextImpl(null, trigger, null);

        processor.execute(context);

        verify(sessionArchiverProducerService, times(2)).archiveSession(any());
    }

}
