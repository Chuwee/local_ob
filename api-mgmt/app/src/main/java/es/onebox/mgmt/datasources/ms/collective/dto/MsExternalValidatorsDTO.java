package es.onebox.mgmt.datasources.ms.collective.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class MsExternalValidatorsDTO implements Serializable {
    private static final long serialVersionUID = -3648709192084953752L;

    private List<MsExternalValidatorDTO> validators;

    public List<MsExternalValidatorDTO> getValidators() {
        return validators;
    }

    public void setValidators(List<MsExternalValidatorDTO> validators) {
        this.validators = validators;
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
