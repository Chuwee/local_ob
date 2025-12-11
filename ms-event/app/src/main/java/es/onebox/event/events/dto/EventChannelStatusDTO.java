package es.onebox.event.events.dto;

import es.onebox.event.events.dto.statusflag.EventChannelReleaseFlagStatus;
import es.onebox.event.events.dto.statusflag.EventChannelSaleFlagStatus;
import es.onebox.event.events.enums.EventChannelStatus;

import java.io.Serializable;

public class EventChannelStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventChannelStatus request;
    private EventChannelSaleFlagStatus sale;
    private EventChannelReleaseFlagStatus release;

    public EventChannelStatus getRequest() {
        return request;
    }

    public void setRequest(EventChannelStatus request) {
        this.request = request;
    }

    public EventChannelSaleFlagStatus getSale() {
        return sale;
    }

    public void setSale(EventChannelSaleFlagStatus sale) {
        this.sale = sale;
    }

    public EventChannelReleaseFlagStatus getRelease() {
        return release;
    }

    public void setRelease(EventChannelReleaseFlagStatus release) {
        this.release = release;
    }

}
