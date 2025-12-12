package es.onebox.common.datasources.common.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Category extends CategoryBase implements Serializable{

    @Serial
    private static final long serialVersionUID = 1961217571917038440L;

    private CategoryBase custom;

    public CategoryBase getCustom() {
        return custom;
    }

    public void setCustom(CategoryBase custom) {
        this.custom = custom;
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
