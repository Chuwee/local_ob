package es.onebox.mgmt.entities.customertypes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTriggerDTO;
import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTypeDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateCustomerTypeDTO implements Serializable {

    @JsonProperty("assignation_type")
    @NotNull
    private AssignationTypeDTO assignationType;
    private List<AssignationTriggerDTO> triggers;
    @NotNull
    private String code;
    @NotNull
    private String name;


    @Serial
    private static final long serialVersionUID = -1508957587305824861L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public AssignationTypeDTO getAssignationType() {
        return assignationType;
    }

    public void setAssignationType(
        AssignationTypeDTO assignationType) {
        this.assignationType = assignationType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssignationTriggerDTO> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<AssignationTriggerDTO> triggers) {
        this.triggers = triggers;
    }
}
