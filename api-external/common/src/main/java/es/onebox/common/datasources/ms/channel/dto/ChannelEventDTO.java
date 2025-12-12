package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.ChannelEventStatus;
import es.onebox.common.datasources.ms.channel.enums.TicketHandlingType;

import java.io.Serializable;

public class ChannelEventDTO implements Serializable {

    private static final long serialVersionUID = -8305754487372905457L;

    private Integer id;
    private Integer channelId;
    private Integer eventId;
    private ChannelEventStatus status;
    private Boolean channelRefundsAllowed;
    private TicketHandlingType ticketHandling;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public ChannelEventStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelEventStatus status) {
        this.status = status;
    }

    public Boolean getChannelRefundsAllowed() {
        return channelRefundsAllowed;
    }

    public void setChannelRefundsAllowed(Boolean channelRefundsAllowed) {
        this.channelRefundsAllowed = channelRefundsAllowed;
    }

    public TicketHandlingType getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(TicketHandlingType ticketHandling) {
        this.ticketHandling = ticketHandling;
    }
}
