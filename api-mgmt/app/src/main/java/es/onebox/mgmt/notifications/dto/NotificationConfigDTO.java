package es.onebox.mgmt.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.entities.dto.EventAction;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.notifications.enums.NotificationsVisible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class NotificationConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private NotificationsScope scope;

    private IdNameDTO operator;

    private IdNameDTO entity;

    private IdNameDTO channel;

    private NotificationsVisible visible;

    @JsonProperty("notification_url")
    private String url;

    @JsonProperty("api_key")
    private String apiKey;

    private NotificationsStatus status;

    private List<EventAction> events;

    @JsonProperty("internal_name")
    private String internalName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotificationsScope getScope() {
        return scope;
    }

    public void setScope(NotificationsScope scope) {
        this.scope = scope;
    }

    public IdNameDTO getOperator() {
        return operator;
    }

    public void setOperator(IdNameDTO operator) {
        this.operator = operator;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public IdNameDTO getChannel() {
        return channel;
    }

    public void setChannel(IdNameDTO channel) {
        this.channel = channel;
    }

    public NotificationsVisible getVisible() {
        return visible;
    }

    public void setVisible(NotificationsVisible visible) {
        this.visible = visible;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<EventAction> getEvents() {
        return events;
    }

    public void setEvents(List<EventAction> events) {
        this.events = events;
    }

    public NotificationsStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationsStatus status) {
        this.status = status;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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
