package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.MsSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class MsSaleRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1094686589977613847L;
    private Long id;
    private MsSaleRequestsStatus status;
    private ZonedDateTime date;
    private MsChannelSaleRequestDTO channel;
    private MsEventSaleRequestDTO event;

    public MsSaleRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MsSaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(MsSaleRequestsStatus status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public MsChannelSaleRequestDTO getChannel() {
        return channel;
    }

    public void setChannel(MsChannelSaleRequestDTO channel) {
        this.channel = channel;
    }

    public MsEventSaleRequestDTO getEvent() {
        return event;
    }

    public void setEvent(MsEventSaleRequestDTO event) {
        this.event = event;
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
