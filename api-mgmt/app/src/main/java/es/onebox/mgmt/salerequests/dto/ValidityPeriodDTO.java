package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.common.BaseValidityPeriodDTO;
import es.onebox.mgmt.salerequests.enums.ValidityPeriodType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ValidityPeriodDTO extends BaseValidityPeriodDTO implements Serializable {
    private static final long serialVersionUID = -4583857978633850924L;

    private ValidityPeriodType type;

    public ValidityPeriodType getType() {
        return type;
    }

    public void setType(ValidityPeriodType type) {
        this.type = type;
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
