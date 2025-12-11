package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class BaseAdditionalData implements Serializable {

    private BigDecimal price;
    private String eventName;
    private String channelName;
    private String sessionName;
    private ZonedDateTime sessionDate;
    private String venueName;
    private String sectorName;
    private String notNumberedAreaName;
    private String rowName;
    private String seatName;
    private String clientName;
    private String username;
    private TicketStatus status;


    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getSessionName() {
        return sessionName;
    }
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public ZonedDateTime getSessionDate() {
        return sessionDate;
    }
    public void setSessionDate(ZonedDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getVenueName() {
        return venueName;
    }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getSectorName() {
        return sectorName;
    }
    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getNotNumberedAreaName() {
        return notNumberedAreaName;
    }

    public void setNotNumberedAreaName(String notNumberedAreaName) {
        this.notNumberedAreaName = notNumberedAreaName;
    }

    public String getRowName() {
        return rowName;
    }
    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSeatName() {
        return seatName;
    }
    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TicketStatus getStatus() {
        return status;
    }
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

}
