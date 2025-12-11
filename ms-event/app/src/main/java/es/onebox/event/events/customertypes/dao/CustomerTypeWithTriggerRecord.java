package es.onebox.event.events.customertypes.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelCustomTypeRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CustomerTypeWithTriggerRecord extends CpanelCustomTypeRecord {

    private AssignationTrigger trigger;
    private String handler;

    public CustomerTypeWithTriggerRecord() {
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
