package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.EventChannelReleaseFlagStatus;
import es.onebox.common.datasources.ms.event.enums.EventChannelSaleFlagStatus;
import es.onebox.common.datasources.ms.event.enums.EventChannelStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventChannelStatusDTO implements Serializable {

    @Serial
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
