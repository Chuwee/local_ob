package es.onebox.common.datasources.distribution.dto.order;

import java.io.Serial;
import java.io.Serializable;

public class DeliveryOptions implements Serializable {
    @Serial
    private static final long serialVersionUID = -3092593370968414828L;

    private EmailTicketsType emailDispatchMode;

    public DeliveryOptions() {
    }

    public EmailTicketsType getEmailDispatchMode() {
        return emailDispatchMode;
    }

    public void setEmailDispatchMode(EmailTicketsType emailDispatchMode) {
        this.emailDispatchMode = emailDispatchMode;
    }
}
