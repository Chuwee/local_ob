package es.onebox.event.sessions.dto.external;

import es.onebox.event.sessions.domain.sessionconfig.DigitalTicketMode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class SessionExternalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4553409974128619844L;

    private DigitalTicketMode digitalTicketMode;

    private Map<String, Object> additionalProperties;

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

}
