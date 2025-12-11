package es.onebox.mgmt.datasources.ms.event.dto.session;



import es.onebox.mgmt.common.DigitalTicketMode;

import java.io.Serial;
import java.io.Serializable;

public class SessionExternalConfig implements Serializable {

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
