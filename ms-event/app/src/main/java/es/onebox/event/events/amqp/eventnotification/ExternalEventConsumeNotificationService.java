package es.onebox.event.events.amqp.eventnotification;

import es.onebox.event.datasources.ms.notification.dto.ExternalNotification;
import es.onebox.event.datasources.ms.notification.repository.NotificationRepository;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.priceengine.request.ChannelStatus;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExternalEventConsumeNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEventConsumeNotificationService.class);

    @Autowired
    private EventChannelDao eventChannelDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    @Qualifier("externalEventConsumeNotificationProducer")
    private DefaultProducer externalEventConsumeNotificationProducer;

    public void notificationEvent(UpdateEventRequestDTO newEvent) {
        List<EventChannelForCatalogRecord> eventChannels = eventChannelDao.getEventChannels(newEvent.getId());

        List<Integer> newEventChannels =
                eventChannels.stream()
                        .filter(c -> c.getEstado() == ChannelStatus.ACTIVE.getId())
                        .map(CpanelEventoCanalRecord::getIdcanal).collect(Collectors.toList());

        List<Integer> notificationsChannels = filterNotificationsChannels(newEventChannels);

        for (Integer channelId : notificationsChannels) {
            notificationEventChannel(newEvent, channelId);
        }
    }

    private List<Integer> filterNotificationsChannels(List<Integer> channelsIds) {
        List<Integer> notificationsChannels = this.getExternalNotificationsChannels();
        return channelsIds.stream().filter(notificationsChannels::contains).collect(Collectors.toList());
    }

    protected List<Integer> getExternalNotificationsChannels() {
        return notificationRepository.getExternalNotifications().stream().map(
                ExternalNotification::getChannelId).collect(Collectors.toList());
    }

    private void notificationEventChannel(UpdateEventRequestDTO newEvent, Integer channelId) {
        ExternalEventConsumeNotificationMessage externalEventConsumeNotificationMessage =
                new ExternalEventConsumeNotificationMessage();
        externalEventConsumeNotificationMessage.setChannelId(channelId);
        externalEventConsumeNotificationMessage.setEventId(newEvent.getId().intValue());

        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setIncludeDeleted(false);
        sessionFilter.setEventId(Collections.singletonList(newEvent.getId()));
        List<Integer> sessionIds = sessionDao.findFlatSessions(sessionFilter).stream()
                .map(CpanelSesionRecord::getIdsesion)
                .collect(Collectors.toList());

        externalEventConsumeNotificationMessage.setSessions(sessionIds);
        // TODO it isn't necesary in update event because we aren't updating this inforamtion
//        externalEventConsumeNotificationMessage.setOldEvent(buildEventCriteria(oldObject));
//        externalEventConsumeNotificationMessage.setNewEvent(buildEventCriteria(newObject));
        sendMessage(externalEventConsumeNotificationMessage);
    }

    public void sendMessage(ExternalEventConsumeNotificationMessage externalEventConsumeNotificationMessage) {
        try {
            externalEventConsumeNotificationProducer.sendMessage(externalEventConsumeNotificationMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] ExternalEventConsumeNotificationService Message could not be send", e);
        }
    }

}
