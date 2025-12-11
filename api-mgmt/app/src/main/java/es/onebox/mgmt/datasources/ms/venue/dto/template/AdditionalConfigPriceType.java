package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;

public class AdditionalConfigPriceType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long avetPriceId;
    private Boolean restrictiveAccess;
    private Long gateId;

    public Long getAvetPriceId() {
        return avetPriceId;
    }

    public void setAvetPriceId(Long avetPriceId) {
        this.avetPriceId = avetPriceId;
    }

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
