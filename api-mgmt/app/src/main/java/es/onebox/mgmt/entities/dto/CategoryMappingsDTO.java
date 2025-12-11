package es.onebox.mgmt.entities.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class CategoryMappingsDTO extends ArrayList<CategoryMappingDTO> {

    public CategoryMappingsDTO() {
    }

    public CategoryMappingsDTO(@NotNull Collection<? extends CategoryMappingDTO> c) {
        super(c);
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
