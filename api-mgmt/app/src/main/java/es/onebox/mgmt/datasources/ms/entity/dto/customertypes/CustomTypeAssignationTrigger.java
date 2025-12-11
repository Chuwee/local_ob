package es.onebox.mgmt.datasources.ms.entity.dto.customertypes;

import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationTrigger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CustomTypeAssignationTrigger implements Serializable {

    private static final long serialVersionUID = 1L;

    private AssignationTrigger trigger;
    private String handler;

    public CustomTypeAssignationTrigger() {
    }

    public CustomTypeAssignationTrigger(String handler, AssignationTrigger trigger) {
        this.handler = handler;
        this.trigger = trigger;
    }

    public AssignationTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(AssignationTrigger trigger) {
        this.trigger = trigger;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
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
