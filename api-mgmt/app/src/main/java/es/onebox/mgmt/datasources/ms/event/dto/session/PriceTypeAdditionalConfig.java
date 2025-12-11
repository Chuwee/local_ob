package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class PriceTypeAdditionalConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean restrictiveAccess;
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
