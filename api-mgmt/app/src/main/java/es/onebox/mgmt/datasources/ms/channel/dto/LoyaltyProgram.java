package es.onebox.mgmt.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class LoyaltyProgram implements Serializable {
    private static final long serialVersionUID = 2L;

    private LoyaltyReception reception;

    public LoyaltyReception getReception() { return reception; }

    public void setReception(LoyaltyReception reception) { this.reception = reception; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
