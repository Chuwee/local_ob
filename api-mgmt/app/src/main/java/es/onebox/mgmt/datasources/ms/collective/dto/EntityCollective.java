package es.onebox.mgmt.datasources.ms.collective.dto;

import java.io.Serializable;
import java.util.Map;

public class EntityCollective implements Serializable {

    private static final long serialVersionUID = -7340219256822056793L;

    private Map<String,Object> externalValidatorProperties;

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
    }
}
