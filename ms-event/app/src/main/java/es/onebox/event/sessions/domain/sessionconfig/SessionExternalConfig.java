package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SessionExternalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -951589429553890475L;

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
