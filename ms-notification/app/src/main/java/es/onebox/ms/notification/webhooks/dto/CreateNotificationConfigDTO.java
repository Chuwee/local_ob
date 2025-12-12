package es.onebox.ms.notification.webhooks.dto;

import es.onebox.ms.notification.webhooks.enums.NotificationType;
import es.onebox.ms.notification.webhooks.enums.NotificationVisible;
import es.onebox.ms.notification.webhooks.enums.NotificationsScope;
import es.onebox.ms.notification.webhooks.enums.NotificationsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CreateNotificationConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationsScope scope;

    private NotificationsStatus status;

    private Long operatorId;

    private Long entityId;

    private Long channelId;

    private Map<NotificationType, List<String>> events;

    private NotificationVisible visible;

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

    public Map<NotificationType, List<String>> getEvents() {
        return events;
    }

    public void setEvents(Map<NotificationType, List<String>> events) {
        this.events = events;
    }

    public NotificationVisible getVisible() {
        return visible;
    }

    public void setVisible(NotificationVisible visible) {
        this.visible = visible;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
