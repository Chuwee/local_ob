package es.onebox.mgmt.entities.customertypes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerTypeDTO extends IdNameCodeDTO implements Serializable {

    @JsonProperty("assignation_type")
    private AssignationTypeDTO assignationType;
    private List<CustomerTypeTriggerDTO> triggers;

    @Serial
    private static final long serialVersionUID = -2014022821271256298L;

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

    public List<CustomerTypeTriggerDTO> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<CustomerTypeTriggerDTO> triggers) {
        this.triggers = triggers;
    }
}
