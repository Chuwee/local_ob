package es.onebox.fusionauth.dto;

import java.io.Serial;
import java.io.Serializable;

public class FusionAuthNotificationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5984215353081896057L;

    private FusionAuthEventDTO event;

    public FusionAuthNotificationDTO() {
    }

    public FusionAuthEventDTO getEvent() {
        return event;
    }

    public void setEvent(FusionAuthEventDTO event) {
        this.event = event;
    }
}
