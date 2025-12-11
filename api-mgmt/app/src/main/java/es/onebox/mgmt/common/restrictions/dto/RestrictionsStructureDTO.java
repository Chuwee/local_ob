package es.onebox.mgmt.common.restrictions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.RestrictionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RestrictionsStructureDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("restriction_type")
    private RestrictionType restrictionType;
    private List<ConfigurationStructureFieldDTO> fields;

    public RestrictionsStructureDTO() {
    }


    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
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
