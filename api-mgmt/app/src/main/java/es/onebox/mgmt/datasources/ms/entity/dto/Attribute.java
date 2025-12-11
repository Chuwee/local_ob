package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long entityId;
    private String name;
    private AttributeTexts texts;
    private String code;
    private Integer scope;
    private Integer type;
    private Integer selectionType;
    private Integer min;
    private Integer max;

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

    public AttributeTexts getTexts() {
        return texts;
    }

    public void setTexts(AttributeTexts texts) {
        this.texts = texts;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(Integer selectionType) {
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
}
