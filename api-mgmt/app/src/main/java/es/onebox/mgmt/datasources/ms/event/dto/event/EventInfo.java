package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class EventInfo implements Serializable {

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
