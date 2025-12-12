package es.onebox.ms.notification.externalnotifications.factory;

import es.onebox.ms.notification.externalnotifications.ExternalNotification;
import es.onebox.ms.notification.externalnotifications.ExternalNotifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalNotificationServiceRouter {

    @Autowired
    private ExternalNotifications externalNotifications;

    public ExternalNotificationServiceEnums getExternalNotificationService(Integer channelId) {
        ExternalNotification externalNotification = externalNotifications.getExternalNotificationByChannel(channelId);
        if (externalNotification == null) {
            return ExternalNotificationServiceEnums.ONEBOX;
        } else if (externalNotification.getService().equals(ExternalNotificationServiceEnums.ATRAPALO_SERVICE.name())) {
            return ExternalNotificationServiceEnums.ATRAPALO_SERVICE;
        }
        return ExternalNotificationServiceEnums.ONEBOX;
    }
}
