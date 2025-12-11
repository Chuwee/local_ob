package es.onebox.mgmt.users.dto;

import es.onebox.mgmt.users.enums.VisibilityType;
import jakarta.validation.constraints.NotNull;

public class UpdateVisibilityDTO {
    @NotNull
    private VisibilityType type;

    public VisibilityType getType() {return type;}

    public void setType(VisibilityType type) {this.type = type;}
}
