package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EntityTextBlockFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<EntityBlockType> type;
    private EntityBlockCategory category;
    private String language;

    public EntityTextBlockFilter() {
    }

    public EntityTextBlockFilter(List<EntityBlockType> type, EntityBlockCategory category, String language) {
        this.type = type;
        this.category = category;
        this.language = language;
    }

    public List<EntityBlockType> getType() {
        return type;
    }

    public void setType(List<EntityBlockType> type) {
        this.type = type;
    }

    public EntityBlockCategory getCategory() {
        return category;
    }

    public void setCategory(EntityBlockCategory category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
