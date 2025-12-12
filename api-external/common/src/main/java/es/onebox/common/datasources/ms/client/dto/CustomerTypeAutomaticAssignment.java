package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class CustomerTypeAutomaticAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    private AssignationTrigger trigger;

    public CustomerTypeAutomaticAssignment() {
    }

    public CustomerTypeAutomaticAssignment(AssignationTrigger trigger) {
        this.trigger = trigger;
    }

    public AssignationTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(AssignationTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
