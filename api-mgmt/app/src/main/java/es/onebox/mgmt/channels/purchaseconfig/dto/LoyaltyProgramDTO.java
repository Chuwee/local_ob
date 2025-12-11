package es.onebox.mgmt.channels.purchaseconfig.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class LoyaltyProgramDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Valid
    private LoyaltyReceptionDTO reception;

    public LoyaltyReceptionDTO getReception() { return reception; }

    public void setReception(LoyaltyReceptionDTO reception) { this.reception = reception; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
