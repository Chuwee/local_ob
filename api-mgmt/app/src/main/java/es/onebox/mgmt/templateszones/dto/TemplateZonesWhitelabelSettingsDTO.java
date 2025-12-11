package es.onebox.mgmt.templateszones.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TemplateZonesWhitelabelSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3511014989920202594L;

    private List<TemplateZoneModuleSettingsDTO> modules;

    public List<TemplateZoneModuleSettingsDTO> getModules() {
        return modules;
    }

    public void setModules(List<TemplateZoneModuleSettingsDTO> modules) {
        this.modules = modules;
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
