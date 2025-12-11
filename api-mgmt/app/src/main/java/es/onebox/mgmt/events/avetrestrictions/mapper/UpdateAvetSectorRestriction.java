package es.onebox.mgmt.events.avetrestrictions.mapper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UpdateAvetSectorRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = -8285367626953251458L;

    private String name;
    private Boolean activated;
    private Map<String, Object> fields;
    private List<Long> venueTemplateSectors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public List<Long> getVenueTemplateSectors() {
        return venueTemplateSectors;
    }

    public void setVenueTemplateSectors(List<Long> venueTemplateSectors) {
        this.venueTemplateSectors = venueTemplateSectors;
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
