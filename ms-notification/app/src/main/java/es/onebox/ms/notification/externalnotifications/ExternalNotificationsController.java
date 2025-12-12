package es.onebox.ms.notification.externalnotifications;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.ms.notification.apis.BaseUriPath.API_BASE;

@RestController
@RequestMapping(ExternalNotificationsController.BASE_URI)
public class ExternalNotificationsController {

    static final String BASE_URI = API_BASE + "/externalNotifications";

    @Autowired
    private ExternalNotifications externalNotifications;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExternalNotification> getExternalNotifications() {
        List<ExternalNotification> externalNotificationsReal = externalNotifications.getExternalNotifications();
        List<ExternalNotification> externalNotificationsCopy = new ArrayList<>();

        for (ExternalNotification externalNotification : externalNotificationsReal) {

            ExternalNotification externalNotification1 = new ExternalNotification();
            externalNotification1.setChannelId(externalNotification.getChannelId());
            externalNotification1.setService(externalNotification.getService());
            externalNotification1.setNotificationUrl(externalNotification.getNotificationUrl());
            externalNotificationsCopy.add(externalNotification1);
        }
        return externalNotificationsCopy;
    }
}
