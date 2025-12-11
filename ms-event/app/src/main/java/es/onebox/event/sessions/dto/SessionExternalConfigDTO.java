package es.onebox.event.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.event.sessions.domain.sessionconfig.DigitalTicketMode;

import java.io.Serial;
import java.io.Serializable;

public class SessionExternalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3459106415943166635L;
    private DigitalTicketMode digitalTicketMode;

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }
}
