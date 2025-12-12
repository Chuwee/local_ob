package es.onebox.ms.notification.externalnotifications.factory;

import es.onebox.ms.notification.externalnotifications.AtrapaloNotificationService;
import es.onebox.ms.notification.externalnotifications.OneboxNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalNotificationServiceFactory extends ExternalNotificationFactorySupport {

    @Autowired
    private OneboxNotificationService oneboxNotificationService;

    @Autowired
    private AtrapaloNotificationService atrapaloNotificationService;

    @Override
    protected ExternalNotificationService getAtrapaloIntegration() {
        return atrapaloNotificationService;
    }

    @Override
    protected ExternalNotificationService getOneboxIntegration() {
        return oneboxNotificationService;
    }

    public ExternalNotificationService getIntegrationService(Integer channelId) {
        return super.getExternalNotificationService(channelId);
    }
}
