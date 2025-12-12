package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.datasources.ms.entity.enums.StreamingVendor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EntityStreaming implements Serializable {

    @Serial
    private static final long serialVersionUID = 4279023479251769238L;
    private Boolean allowed;
    private Boolean enabled;
    private List<StreamingVendor> vendor;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<StreamingVendor> getVendor() {
        return vendor;
    }

    public void setVendor(List<StreamingVendor> vendor) {
        this.vendor = vendor;
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
