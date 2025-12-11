package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.datasources.ms.event.dto.event.statusflag.EventChannelReleaseFlagStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.statusflag.EventChannelSaleFlagStatus;

import java.io.Serializable;

public class EventChannelStatusInfo implements Serializable {

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
