package es.onebox.mgmt.events.dto.channel;

import es.onebox.mgmt.events.enums.EventStatus;

import java.io.Serializable;

public class EventInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private EventStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

}
