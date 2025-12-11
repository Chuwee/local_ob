package es.onebox.mgmt.events.avetrestrictions.mapper;

import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AvetSectorRestrictionCreate implements Serializable {

    @Serial
    private static final long serialVersionUID = -8285367626953251458L;

    private String name;
    private AvetSectorRestrictionType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AvetSectorRestrictionType getType() {
        return type;
    }

    public void setType(AvetSectorRestrictionType type) {
        this.type = type;
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

