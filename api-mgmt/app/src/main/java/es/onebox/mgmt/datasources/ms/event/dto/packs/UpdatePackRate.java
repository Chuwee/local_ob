package es.onebox.mgmt.datasources.ms.event.dto.packs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdatePackRate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Boolean defaultRate;

    private Boolean restrictive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDefault() {
        return defaultRate;
    }

    public void setDefault(Boolean aDefault) {
        defaultRate = aDefault;
    }

    public Boolean getRestrictive() {
        return restrictive;
    }

    public void setRestrictive(Boolean restrictive) {
        this.restrictive = restrictive;
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
