package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProducerInoivcePrefixFilter extends BaseRequestFilter {

    private Boolean isDefault;

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefult(Boolean isDefault) {
        this.isDefault = isDefault;
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
