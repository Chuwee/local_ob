package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SearchEntitiesResponse {

    private static final long serialVersionUID = 1L;

    private List<EntityDTO> data;

    public List<EntityDTO> getData() {
        return data;
    }

    public void setData(List<EntityDTO> data) {
        this.data = data;
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
