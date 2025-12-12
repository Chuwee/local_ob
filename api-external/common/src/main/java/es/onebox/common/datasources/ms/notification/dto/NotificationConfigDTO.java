package es.onebox.common.datasources.ms.notification.dto;

import es.onebox.common.datasources.ms.notification.enums.NotificationType;
import es.onebox.common.datasources.ms.notification.enums.NotificationsScope;
import es.onebox.common.datasources.ms.notification.enums.NotificationsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NotificationConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String documentId;
    private NotificationsScope scope;
    private Long operatorId;
    private Long entityId;
    private Long channelId;
    private NotificationsStatus status;
    private Map<NotificationType, List<String>> events;
    private String url;
    private String apiKey; //churro

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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
