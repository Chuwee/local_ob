package es.onebox.mgmt.users.dto;

import es.onebox.mgmt.users.enums.VisibilityType;

import java.io.Serial;
import java.io.Serializable;

public class VisibilityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private VisibilityType type;

    public VisibilityType getType() {return type;}

    public void setType(VisibilityType type) {this.type = type;}
}
