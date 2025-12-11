package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class PriceTypeAdditionalConfigRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long gateId;

    public Long getGateId() {
        return gateId;
    }

    public void setGateId(Long gateId) {
        this.gateId = gateId;
    }

}
