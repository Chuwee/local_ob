package es.onebox.event.products.dto;

import es.onebox.event.sessions.dto.SessionStatus;

import java.io.Serializable;

public class ProductChannelSessionDTO extends ProductSessionBaseDTO implements Serializable {

    private SessionStatus status;

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
