package es.onebox.event.events.amqp.eventnotification;

import es.onebox.event.datasources.ms.notification.dto.ExternalNotification;
import es.onebox.event.datasources.ms.notification.repository.NotificationRepository;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.priceengine.request.ChannelStatus;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class ExternalEventConsumeNotificationServiceTest {

    @InjectMocks
    private ExternalEventConsumeNotificationService externalEventConsumeNotificationService;

    @Mock
    private EventChannelDao eventChannelDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private DefaultProducer externalEventConsumeNotificationProducer;

    @Captor
    private ArgumentCaptor<ExternalEventConsumeNotificationMessage> externalEventConsumeNotificationMessageCapture;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findEventsTestOK() throws Exception {
        Long eventId = 666l;

        EventDTO oldEvent = new EventDTO();
        oldEvent.setId(eventId);

        UpdateEventRequestDTO newEvent = new UpdateEventRequestDTO();
        newEvent.setId(eventId);

        List<EventChannelForCatalogRecord> eventChannels = new ArrayList<>();
        eventChannels.add(getCpanelEventoCanalRecord(11, ChannelStatus.BLOCKED));
        eventChannels.add(getCpanelEventoCanalRecord(12, ChannelStatus.ACTIVE));
        eventChannels.add(getCpanelEventoCanalRecord(13, ChannelStatus.ACTIVE));

        List<ExternalNotification> externalNotificationList = new ArrayList<>();
        ExternalNotification externalNotification = new ExternalNotification();
        externalNotification.setChannelId(12);
        externalNotificationList.add(externalNotification);

        Mockito.when(eventChannelDao.getEventChannels(Mockito.anyLong())).thenReturn(eventChannels);
        Mockito.when(notificationRepository.getExternalNotifications()).thenReturn(externalNotificationList);
        Mockito.when(sessionDao.findFlatSessions(Mockito.any())).thenReturn(new ArrayList<>());

        externalEventConsumeNotificationService.notificationEvent(newEvent);

        Mockito.verify(externalEventConsumeNotificationProducer)
                .sendMessage(externalEventConsumeNotificationMessageCapture.capture());

        Assertions.assertEquals(externalEventConsumeNotificationMessageCapture.getValue().getChannelId().intValue(), 12);
        Assertions.assertEquals(externalEventConsumeNotificationMessageCapture.getValue().getEventId().intValue(), newEvent.getId().intValue());
    }

    private EventChannelForCatalogRecord getCpanelEventoCanalRecord(int channelId, ChannelStatus channelState) {
        EventChannelForCatalogRecord cpanelEventoCanalRecord = new EventChannelForCatalogRecord();
        cpanelEventoCanalRecord.setIdcanal(channelId);
        cpanelEventoCanalRecord.setEstado(channelState.getId());
        return cpanelEventoCanalRecord;
    }

}
