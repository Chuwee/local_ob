package es.onebox.mgmt.channels.purchaseconfig.dto;

import es.onebox.mgmt.channels.purchaseconfig.enums.LoyaltyReceptionTypeDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class LoyaltyReceptionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private LoyaltyReceptionTypeDTO type;
    @Min(0)
    private Integer hours;

    public LoyaltyReceptionTypeDTO getType() { return type; }

    public void setType(LoyaltyReceptionTypeDTO type) { this.type = type; }

    public Integer getHours() { return hours; }

    public void setHours(Integer hours) { this.hours = hours; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
