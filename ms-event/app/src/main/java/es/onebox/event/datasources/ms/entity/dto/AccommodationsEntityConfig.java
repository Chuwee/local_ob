package es.onebox.event.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AccommodationsEntityConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<AccommodationsVendor> allowedVendors;

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<AccommodationsVendor> getAllowedVendors() {
        return allowedVendors;
    }
    public void setAllowedVendors(List<AccommodationsVendor> allowedVendors) {
        this.allowedVendors = allowedVendors;
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
