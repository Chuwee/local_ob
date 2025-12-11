package es.onebox.mgmt.datasources.ms.insurance.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class CancellationService extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 7590200564328381417L;

    private Boolean enabled;
    private Boolean defaultAllowed;
    private Boolean defaultSelected;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDefaultAllowed() {
        return defaultAllowed;
    }

    public void setDefaultAllowed(Boolean defaultAllowed) {
        this.defaultAllowed = defaultAllowed;
    }

    public Boolean getDefaultSelected() {
        return defaultSelected;
    }

    public void setDefaultSelected(Boolean defaultSelected) {
        this.defaultSelected = defaultSelected;
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
