package es.onebox.ms.notification.externalnotifications.factory;

import es.onebox.ms.notification.externalnotifications.event.ExternalEventConsumeNotificationMessage;

public interface ExternalNotificationService {

    void notificationEvent(ExternalEventConsumeNotificationMessage eventConsumeNotificationMessage) throws Exception;

}
