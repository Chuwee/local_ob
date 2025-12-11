package es.onebox.mgmt.datasources.ms.entity.dto.customertypes;

import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerType implements Serializable {

    @Serial
    private static final long serialVersionUID = -7600221463076453959L;

    private Long id;
    private String code;
    private String name;
    private AssignationType assignationType;
    private List<CustomTypeAssignationTrigger> triggers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AssignationType getAssignationType() {
        return assignationType;
    }

    public void setAssignationType(
        AssignationType assignationType) {
        this.assignationType = assignationType;
    }

    public List<CustomTypeAssignationTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<CustomTypeAssignationTrigger> triggers) {
        this.triggers = triggers;
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
