package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class CreateSessionSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enableOrphanSeats;


    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }
}
