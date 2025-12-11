package es.onebox.event.events.avetrestrictions.dto;

import es.onebox.event.events.avetrestrictions.enums.AvetSectorRestrictionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class AvetSectorRestrictionCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8285367626953251458L;

    @NotNull
    @Length(min = 1, max = 75, message = "restriction_name max size 75")
    private String name;
    @NotNull
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
