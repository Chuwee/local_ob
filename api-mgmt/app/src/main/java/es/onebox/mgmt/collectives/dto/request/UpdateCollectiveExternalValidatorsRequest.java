package es.onebox.mgmt.collectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

public class UpdateCollectiveExternalValidatorsRequest implements Serializable {
    private static final long serialVersionUID = 9136166899896374425L;

    @JsonProperty("external_validator_properties")
    private Map<String,Object> externalValidatorProperties;
    @JsonProperty("entity_id")
    private Long entityId;

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}