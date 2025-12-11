package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class BaseEventChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private EventChannelInfo channel;
    private EventInfo event;
    private EventChannelStatusInfo status;
    private EventChannelSettings settings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventChannelInfo getChannel() {
        return channel;
    }

    public void setChannel(EventChannelInfo channel) {
        this.channel = channel;
    }

    public EventInfo getEvent() {
        return event;
    }

    public void setEvent(EventInfo event) {
        this.event = event;
    }

    public EventChannelStatusInfo getStatus() {
        return status;
    }

    public void setStatus(EventChannelStatusInfo status) {
        this.status = status;
    }

    public EventChannelSettings getSettings() {
        return settings;
    }

    public void setSettings(EventChannelSettings settings) {
        this.settings = settings;
    }

}
