package es.onebox.mgmt.collectives.dto.request;

import es.onebox.mgmt.collectives.dto.Status;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class UpdateCollectiveStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
