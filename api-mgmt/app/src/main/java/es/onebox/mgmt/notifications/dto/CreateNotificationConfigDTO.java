package es.onebox.mgmt.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.dto.EventAction;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.validation.annotation.UrlWithPortFormat;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class CreateNotificationConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Scope must not be null")
    private NotificationsScope scope;

    private List<EventAction> events;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("channel_id")
    private Long channelId;

    @JsonProperty("operator_id")
    private Long operatorId;

    @JsonProperty("notification_url")
    @UrlWithPortFormat(message = "Url value must be a valid URL")
    private String url;

    @JsonProperty("internal_name")
    private String internalName;

    public NotificationsScope getScope() {
        return scope;
    }

    public void setScope(NotificationsScope scope) {
        this.scope = scope;
    }

    public List<EventAction> getEvents() {
        return events;
    }

    public void setEvents(List<EventAction> events) {
        this.events = events;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
