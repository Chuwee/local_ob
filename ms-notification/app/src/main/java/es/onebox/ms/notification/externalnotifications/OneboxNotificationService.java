package es.onebox.ms.notification.externalnotifications;


import es.onebox.ms.notification.externalnotifications.event.ExternalEventConsumeNotificationMessage;
import es.onebox.ms.notification.externalnotifications.factory.ExternalNotificationService;
import org.springframework.stereotype.Service;

@Service
public class OneboxNotificationService implements ExternalNotificationService {

    @Override
    public void notificationEvent(ExternalEventConsumeNotificationMessage eventConsumeNotificationMessage) {
    }

}
