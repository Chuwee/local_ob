package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityVisibilityRelationType;

public class EntityOperatorVisibility extends IdNameDTO {

    private EntityVisibilityRelationType relationType;

    public EntityOperatorVisibility() {
    }

    public EntityVisibilityRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(EntityVisibilityRelationType relationType) {
        this.relationType = relationType;
    }
}
