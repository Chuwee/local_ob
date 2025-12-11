package es.onebox.mgmt.templateszones.dto;

import es.onebox.mgmt.channels.enums.ThankYouPageModuleTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TemplateZoneModuleSettingsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6489141018649421736L;

    private ThankYouPageModuleTypeDTO type;
    private Integer blockId;
    private Boolean enabled;
    private Boolean visible;

    public ThankYouPageModuleTypeDTO getType() {
        return type;
    }

    public void setType(ThankYouPageModuleTypeDTO type) {
        this.type = type;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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