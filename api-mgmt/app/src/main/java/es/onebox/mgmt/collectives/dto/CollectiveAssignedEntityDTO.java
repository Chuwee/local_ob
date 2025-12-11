package es.onebox.mgmt.collectives.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class CollectiveAssignedEntityDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    @JsonProperty("external_validator_properties")
    private Map<String,Object> externalValidatorProperties;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
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
