package es.onebox.ms.notification.externalnotifications;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties
public class ExternalNotifications {

    private List<ExternalNotification> externalNotifications;

    public List<ExternalNotification> getExternalNotifications() {
        return externalNotifications;
    }

    public void setExternalNotifications(List<ExternalNotification> externalNotifications) {
        this.externalNotifications = externalNotifications;
    }

    public ExternalNotification getExternalNotificationByChannel(Integer channelId) {
        ExternalNotification externalNotification =
                externalNotifications.stream().
                        filter(e -> e.getChannelId().equals(channelId)).findFirst().orElse(null);
        return externalNotification;
    }
}
