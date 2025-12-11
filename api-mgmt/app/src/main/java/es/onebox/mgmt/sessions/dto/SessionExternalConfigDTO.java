package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.DigitalTicketMode;

import java.io.Serial;
import java.io.Serializable;

public class SessionExternalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4821706068281436541L;
    @JsonProperty(value = "digital_ticket_mode")
    private DigitalTicketMode digitalTicketMode;

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }
}
