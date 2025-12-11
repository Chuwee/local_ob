package es.onebox.mgmt.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EntityCategoryRequestDTO extends BaseCategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("base_category_id")
    private List<Long> baseCategoryId;

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
