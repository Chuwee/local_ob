package es.onebox.mgmt.venues.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.venues.enums.ExternalPluginType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ExternalPluginDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -3708715263903588916L;

    private ExternalPluginType type;
    private Boolean enabled;

    public ExternalPluginType getType() {
        return type;
    }

    public void setType(ExternalPluginType type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
