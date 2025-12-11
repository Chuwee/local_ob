package es.onebox.event.events.dto;

import java.io.Serializable;

public class BaseEventChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private EventChannelInfoDTO channel;
    private EventInfoDTO event;
    private EventChannelStatusDTO status;
    private EventChannelSettingsDTO settings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(EventChannelInfoDTO channel) {
        this.channel = channel;
    }

    public EventChannelStatusDTO getStatus() {
        return status;
    }

    public void setStatus(EventChannelStatusDTO status) {
        this.status = status;
    }

    public EventChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EventChannelSettingsDTO settings) {
        this.settings = settings;
    }

    public EventInfoDTO getEvent() {
        return event;
    }

    public void setEvent(EventInfoDTO event) {
        this.event = event;
    }
}
