package es.onebox.mgmt.secondarymarket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ResalePriceDTO implements Serializable  {

    @Serial
    private static final long serialVersionUID = 2L;

    private ResalePriceTypeDTO type;

    private RestrictionsDTO restrictions;

    public ResalePriceTypeDTO getType() {
        return type;
    }

    public void setType(ResalePriceTypeDTO type) {
        this.type = type;
    }

    public RestrictionsDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(RestrictionsDTO restrictions) {
        this.restrictions = restrictions;
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
