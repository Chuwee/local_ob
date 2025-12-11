package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import es.onebox.mgmt.channels.forms.dto.DynamicFormValidatorDTO;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelFormField implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "key must not be empty")
    private String key;
    private String name;
    private Boolean mandatory;
    private Boolean visible;
    private Boolean mutable;
    private Boolean uneditable;
    private String type;
    private List<DynamicFormValidatorDTO> rules;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DynamicFormValidatorDTO> getRules() {
        return rules;
    }

    public void setRules(List<DynamicFormValidatorDTO> rules) {
        this.rules = rules;
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
