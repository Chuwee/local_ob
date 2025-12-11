package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.datasources.ms.channel.enums.LoyaltyReceptionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class LoyaltyReception implements Serializable {
    private static final long serialVersionUID = 2L;

    private LoyaltyReceptionType type;
    private Integer hours;

    public LoyaltyReceptionType getType() { return type; }

    public void setType(LoyaltyReceptionType type) { this.type = type; }

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
