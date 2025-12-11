package es.onebox.mgmt.entities.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.entities.enums.VisibilityRelationType;

public class EntityOperatorVisibilityDTO extends IdNameDTO {

    private VisibilityRelationType type;

    public EntityOperatorVisibilityDTO() {
    }

    public VisibilityRelationType getType() {
        return type;
    }

    public void setType(VisibilityRelationType type) {
        this.type = type;
    }
}
