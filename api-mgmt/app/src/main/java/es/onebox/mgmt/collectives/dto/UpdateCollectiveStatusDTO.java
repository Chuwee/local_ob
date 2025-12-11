package es.onebox.mgmt.collectives.dto;


import java.io.Serializable;

public class UpdateCollectiveStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
