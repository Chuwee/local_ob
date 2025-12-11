package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.sessions.domain.sessionconfig.DigitalTicketMode;

import java.io.Serial;
import java.io.Serializable;

public class EventExternalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -951589429553890475L;

    private DigitalTicketMode digitalTicketMode;

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }
}
