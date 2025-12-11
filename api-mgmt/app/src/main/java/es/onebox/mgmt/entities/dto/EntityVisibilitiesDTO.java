package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.enums.EntityVisibilityType;

import java.io.Serializable;
import java.util.List;

public class EntityVisibilitiesDTO implements Serializable {

    @JsonProperty("type")
    private EntityVisibilityType visibilityType;

    @JsonProperty("visible_operators")
    private List<EntityOperatorVisibilityDTO> visibleOperators;

    @JsonProperty("visible_entities")
    private List<EntityVisibilityDTO> visibleEntities;

    public EntityVisibilityType getVisibilityType() {
        return visibilityType;
    }

    public void setVisibilityType(EntityVisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public List<EntityOperatorVisibilityDTO> getVisibleOperators() {
        return visibleOperators;
    }

    public void setVisibleOperators(List<EntityOperatorVisibilityDTO> visibleOperators) {
        this.visibleOperators = visibleOperators;
    }

    public List<EntityVisibilityDTO> getVisibleEntities() {
        return visibleEntities;
    }

    public void setVisibleEntities(List<EntityVisibilityDTO> visibleEntities) {
        this.visibleEntities = visibleEntities;
    }
}
