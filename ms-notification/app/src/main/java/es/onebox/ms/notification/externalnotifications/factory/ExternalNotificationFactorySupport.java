package es.onebox.ms.notification.externalnotifications.factory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class ExternalNotificationFactorySupport {

    @Autowired
    protected ExternalNotificationServiceRouter externalNotificationServiceRouter;


    protected ExternalNotificationService getExternalNotificationService(Integer channelId) {

        ExternalNotificationServiceEnums externalNotificationServiceType =
                externalNotificationServiceRouter.getExternalNotificationService(channelId);
        return getIntegrationService(externalNotificationServiceType);
    }

    protected final ExternalNotificationService getIntegrationService(ExternalNotificationServiceEnums externalNotificationServiceType) {
        ExternalNotificationService externalNotificationService = null;

        switch (externalNotificationServiceType) {
            case ONEBOX:
                externalNotificationService = this.getOneboxIntegration();
                break;
            case ATRAPALO_SERVICE:
                externalNotificationService = this.getAtrapaloIntegration();
                break;
            default:
                externalNotificationService = this.getOneboxIntegration();
        }

        return externalNotificationService;
    }

    protected abstract ExternalNotificationService getAtrapaloIntegration();

    protected abstract ExternalNotificationService getOneboxIntegration();


}
