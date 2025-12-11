package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PhoneValidatorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6168648764579959130L;

    private Boolean enabled;

    @JsonProperty("validator_id")
    private String validatorId;

    @JsonProperty("available_validators")
    private List<String> validatorIds;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public List<String> getValidatorIds() { return validatorIds; }

    public void setValidatorIds(List<String> validatorIds) { this.validatorIds = validatorIds; }
}
