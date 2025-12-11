package es.onebox.mgmt.entities.customertypes.dto;


import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTriggerDTO;

import java.io.Serial;
import java.io.Serializable;

public class CustomerTypeTriggerDTO implements Serializable {

    private AssignationTriggerDTO trigger;
    private String handler;
    private boolean selected;


    public AssignationTriggerDTO getTrigger() {
        return trigger;
    }

    public void setTrigger(AssignationTriggerDTO trigger) {
        this.trigger = trigger;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Serial
    private static final long serialVersionUID = 1L;

}
