package es.onebox.mgmt.common.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CommonSurchargeDTO extends SurchargeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SurchargeLimitDTO limit;

    public SurchargeLimitDTO getLimit() {
        return limit;
    }

    public void setLimit(SurchargeLimitDTO limit) {
        this.limit = limit;
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
