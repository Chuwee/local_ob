package es.onebox.mgmt.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.dto.EventAction;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.validation.annotation.UrlWithPortFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateNotificationConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationsStatus status;

    private List<EventAction> events;

    @JsonProperty("notification_url")
    @UrlWithPortFormat(message = "Url value must be a valid URL")
    private String url;

    @JsonProperty("internal_name")
    private String internalName;

    public NotificationsStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationsStatus status) {
        this.status = status;
    }

    public List<EventAction> getEvents() {
        return events;
    }

    public void setEvents(List<EventAction> events) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
