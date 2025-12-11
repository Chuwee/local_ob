package es.onebox.event.datasources.ms.notification.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.event.datasources.ms.notification.MsNotificationDatasource;
import es.onebox.event.datasources.ms.notification.dto.ExternalNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository {

    private final MsNotificationDatasource msNotificationDatasource;

    @Autowired
    public NotificationRepository(MsNotificationDatasource msNotificationDatasource) {
        this.msNotificationDatasource = msNotificationDatasource;
    }

    @Cached(key = "getExternalNotificationsChannels", expires = 3600)
    public List<ExternalNotification> getExternalNotifications() {
        return msNotificationDatasource.getExternalNotifications();
    }

}
