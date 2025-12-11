package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.enums.AttributeScope;
import es.onebox.mgmt.entities.enums.AttributeSelectionType;
import es.onebox.mgmt.entities.enums.AttributeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public class AttributeDTO {

    private Long id;
    @JsonProperty("entity_id")
    private Long entityId;
    private String name;
    private AttributeTextsDTO texts;
    private String code;
    private AttributeScope scope;
    private AttributeType type;
    @JsonProperty("selection_type")
    private AttributeSelectionType selectionType;
    @Min(0)
    @Max(50)
    private Integer min;
    @Min(0)
    @Max(50)
    private Integer max;
    private String value;
    @JsonProperty("selected")
    private List<Long> selectedValuesIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeTextsDTO getTexts() {
        return texts;
    }

    public void setTexts(AttributeTextsDTO texts) {
        this.texts = texts;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AttributeScope getScope() {
        return scope;
    }

    public void setScope(AttributeScope scope) {
        this.scope = scope;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public AttributeSelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(AttributeSelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Long> getSelectedValuesIds() {
        return selectedValuesIds;
    }

    public void setSelectedValuesIds(List<Long> selectedValuesIds) {
        this.selectedValuesIds = selectedValuesIds;
    }

}
