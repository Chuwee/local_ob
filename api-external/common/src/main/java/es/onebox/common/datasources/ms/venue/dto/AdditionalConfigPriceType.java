package es.onebox.common.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalConfigPriceType implements Serializable {
    @Serial
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
