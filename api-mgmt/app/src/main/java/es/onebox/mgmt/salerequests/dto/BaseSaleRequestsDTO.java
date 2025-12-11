package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BaseSaleRequestsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private SaleRequestsStatus status;
    private ZonedDateTime date;
    private ChannelSaleRequestDTO channel;
    private EventSaleRequestDTO event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(SaleRequestsStatus status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ChannelSaleRequestDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelSaleRequestDTO channel) {
        this.channel = channel;
    }

    public EventSaleRequestDTO getEvent() {
        return event;
    }

    public void setEvent(EventSaleRequestDTO event) {
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
