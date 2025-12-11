package es.onebox.event.events.dto;

import java.io.Serializable;

public class RequestSalesEventChannelDTO implements Serializable {

    private static final long serialVersionUID = -4188552469540900094L;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
