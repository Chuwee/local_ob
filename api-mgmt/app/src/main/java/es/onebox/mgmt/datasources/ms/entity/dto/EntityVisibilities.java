package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.EntityVisibilityType;

import java.io.Serializable;
import java.util.List;

public class EntityVisibilities implements Serializable {

    private static final long serialVersionUID = 1L;

    private EntityVisibilityType type;

    private List<EntityOperatorVisibility> visibleOperators;

    private List<EntityVisibility> visibleEntities;


    public EntityVisibilityType getType() {
        return type;
    }

    public void setType(EntityVisibilityType type) {
        this.type = type;
    }

    public List<EntityOperatorVisibility> getVisibleOperators() {
        return visibleOperators;
    }

    public void setVisibleOperators(List<EntityOperatorVisibility> visibleOperators) {
        this.visibleOperators = visibleOperators;
    }

    public List<EntityVisibility> getVisibleEntities() {
        return visibleEntities;
    }

    public void setVisibleEntities(List<EntityVisibility> visibleEntities) {
        this.visibleEntities = visibleEntities;
    }
}
