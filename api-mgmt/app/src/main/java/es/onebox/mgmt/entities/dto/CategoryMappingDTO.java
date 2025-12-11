package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CategoryMappingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("base_category_id")
    private Long baseCategoryId;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBaseCategoryId() {
        return baseCategoryId;
    }

    public void setBaseCategoryId(Long baseCategoryId) {
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
