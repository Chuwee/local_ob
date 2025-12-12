package es.onebox.ms.notification.webhooks.dto;

import es.onebox.couchbase.annotations.Id;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import es.onebox.ms.notification.webhooks.enums.NotificationVisible;
import es.onebox.ms.notification.webhooks.enums.NotificationsScope;
import es.onebox.ms.notification.webhooks.enums.NotificationsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class NotificationConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String documentId;
    private NotificationsScope scope;
    private NotificationsStatus status;
    private Long operatorId;
    private Long entityId;
    private Long channelId;
    private Map<NotificationType, List<String>> events;
    private NotificationVisible visible;
    private String url;
    private String apiKey;
    private String internalName;
    private ZonedDateTime createdAt;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

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

    public String getApiKey() {
        return apiKey;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
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
