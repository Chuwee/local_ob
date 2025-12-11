package es.onebox.mgmt.datasources.ms.client.dto;

import java.io.Serializable;
import java.util.List;

public class PhoneValidatorEntityConfig implements Serializable {

    private Integer entityId;

    private Boolean enabled;

    private String validatorId;

    private List<String> validatorIds;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer id) {
        this.entityId = id;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public List<String> getValidatorIds() { return validatorIds; }

    public void setValidatorIds(List<String> validatorIds) { this.validatorIds = validatorIds; }

}
