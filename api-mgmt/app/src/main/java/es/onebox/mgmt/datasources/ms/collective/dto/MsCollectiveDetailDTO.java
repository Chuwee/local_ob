package es.onebox.mgmt.datasources.ms.collective.dto;

import java.util.Map;

public class MsCollectiveDetailDTO extends MsCollectiveDTO {

    private static final long serialVersionUID = -1169290024388464196L;

    private Map<String,Object> externalValidatorProperties;

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
    }
}