package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PriceTypeAdditionalConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;
    @JsonProperty("gate_id")
    private Long gateId;

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
    }

    public Long getGateId() {
        return gateId;
    }

    public void setGateId(Long gateId) {
        this.gateId = gateId;
    }

}
