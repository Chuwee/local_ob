package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class CreateSessionSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enableOrphanSeats;


    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }
}
