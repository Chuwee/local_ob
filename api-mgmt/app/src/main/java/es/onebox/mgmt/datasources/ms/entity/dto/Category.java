package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public Category(Integer id, Integer parentId, String code, String description) {
        this.id = id;
        this.parentId = parentId;
        this.code = code;
        this.description = description;
    }

    public Category() {
    }

    private Integer id;
    private Integer parentId;
    private String code;
    private String description;
    private List<Long> baseCategoryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getBaseCategoryId() {
        return baseCategoryId;
    }

    public void setBaseCategoryId(List<Long> baseCategoryId) {
        this.baseCategoryId = baseCategoryId;
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
