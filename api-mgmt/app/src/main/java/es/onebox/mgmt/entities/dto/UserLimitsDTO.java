package es.onebox.mgmt.entities.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UserLimitsDTO implements Serializable {

    private static final long serialVersionUID = 2344444597399046651L;
    private BIUserLimitsDTO bi;

    public BIUserLimitsDTO getBi() {
        return bi;
    }

    public void setBi(BIUserLimitsDTO bi) {
        this.bi = bi;
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
