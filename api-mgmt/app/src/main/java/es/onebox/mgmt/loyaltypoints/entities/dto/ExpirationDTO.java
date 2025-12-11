package es.onebox.mgmt.loyaltypoints.entities.dto;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ExpirationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @Min(0)
    private Integer months;

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getMonths() { return months; }

    public void setMonths(Integer months) { this.months = months; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
