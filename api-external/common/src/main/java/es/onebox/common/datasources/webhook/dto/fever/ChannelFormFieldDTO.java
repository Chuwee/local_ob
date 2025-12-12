package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class ChannelFormFieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    @JsonProperty("external_key")
    private String externalKey;
    private Boolean mandatory;
    private Boolean visible;
    private Boolean mutable;
    private Boolean uneditable;
    @JsonProperty("available_rules")
    private List<DynamicFormValidatorInfoDTO> availableRules;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getMutable() {
        return mutable;
    }

    public void setMutable(Boolean mutable) {
        this.mutable = mutable;
    }

    public Boolean getUneditable() {
        return uneditable;
    }

    public void setUneditable(Boolean uneditable) {
        this.uneditable = uneditable;
    }

    public List<DynamicFormValidatorInfoDTO> getAvailableRules() {
        return availableRules;
    }

    public void setAvailableRules(List<DynamicFormValidatorInfoDTO> availableRules) {
        this.availableRules = availableRules;
    }

}
