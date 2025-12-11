package es.onebox.mgmt.events.avetrestrictions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AvetSectorRestrictionStructureDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("restriction_type")
    private AvetSectorRestrictionType restrictionType;
    private List<ConfigurationStructureFieldDTO> fields;

    public AvetSectorRestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(AvetSectorRestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public List<ConfigurationStructureFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<ConfigurationStructureFieldDTO> fields) {
        this.fields = fields;
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