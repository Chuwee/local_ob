package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PriceTypeAdditionalConfigRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("gate_id")
    private Long gateId;

    public Long getGateId() {
        return gateId;
    }

    public void setGateId(Long gateId) {
        this.gateId = gateId;
    }

}
