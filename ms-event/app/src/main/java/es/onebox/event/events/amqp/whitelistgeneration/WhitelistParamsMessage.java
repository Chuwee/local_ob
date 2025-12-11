package es.onebox.event.events.amqp.whitelistgeneration;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by mmolinero on 23/06/18.
 */
public class WhitelistParamsMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Integer idEntidad;
    private Integer gapDays;
    private Integer sessionId;
    private Integer eventId;
    private Integer venueId;
    private BigInteger ticketGroupId;
    private List<Integer> sessionIds;
    private List<String> newBarcodes;

    public Integer getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Integer idEntidad) {
        this.idEntidad = idEntidad;
    }

    public Integer getGapDays() {
        return gapDays;
    }

    public void setGapDays(Integer gapDays) {
        this.gapDays = gapDays;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public BigInteger getTicketGroupId() {
        return ticketGroupId;
    }

    public void setTicketGroupId(BigInteger ticketGroupId) {
        this.ticketGroupId = ticketGroupId;
    }

    public List<Integer> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Integer> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<String> getNewBarcodes() {
        return newBarcodes;
    }

    public void setNewBarcodes(List<String> newBarcodes) {
        this.newBarcodes = newBarcodes;
    }
}
