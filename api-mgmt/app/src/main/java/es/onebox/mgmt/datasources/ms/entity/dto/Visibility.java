package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.users.enums.VisibilityType;

import java.io.Serializable;

public class Visibility implements Serializable {
    private static final long serialVersionUID = 1L;

    private VisibilityType type;

    public VisibilityType getType() {return type;}

    public void setType(VisibilityType type) {this.type = type;}
}
