package es.onebox.mgmt.datasources.ms.notification.dto;

import es.onebox.mgmt.notifications.enums.NotificationType;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.notifications.enums.NotificationsVisible;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CreateNotificationConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationsScope scope;
    private NotificationsStatus status;
    private Long operatorId;
    private Long entityId;
    private Long channelId;
    private NotificationsVisible visible;
    private Map<NotificationType, List<String>> events;
    private String url;
    private String internalName;

    public NotificationsScope getScope() {
        return scope;
    }

    public void setScope(NotificationsScope scope) {
        this.scope = scope;
    }

    public NotificationsStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationsStatus status) {
        this.status = status;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public NotificationsVisible getVisible() {
        return visible;
    }

    public void setVisible(NotificationsVisible visible) {
        this.visible = visible;
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
