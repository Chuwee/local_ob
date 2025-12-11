package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class CancellationServiceDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 2415294994314834441L;

    private Boolean enabled;
    @JsonProperty("default_allowed")
    private Boolean defaultAllowed;
    @JsonProperty("default_selected")
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
