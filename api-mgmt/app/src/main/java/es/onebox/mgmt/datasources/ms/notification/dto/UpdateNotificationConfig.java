package es.onebox.mgmt.datasources.ms.notification.dto;

import es.onebox.mgmt.notifications.enums.NotificationType;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UpdateNotificationConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationsStatus status;
    private Map<NotificationType, List<String>> events;
    private String url;
    private String internalName;

    public NotificationsStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationsStatus status) {
        this.status = status;
    }

    public Map<NotificationType, List<String>> getEvents() {
        return events;
    }

    public void setEvents(Map<NotificationType, List<String>> events) {
        this.events = events;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }
}
