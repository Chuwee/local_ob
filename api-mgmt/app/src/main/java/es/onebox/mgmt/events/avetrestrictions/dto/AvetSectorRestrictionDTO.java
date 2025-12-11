package es.onebox.mgmt.events.avetrestrictions.dto;

import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;

import java.io.Serial;
import java.io.Serializable;

public class AvetSectorRestrictionDTO implements Serializable {
    @Serial
    private static final  long serialVersionUID = -8285367626953251451L;

    private String sid;
    private String name;
    private AvetSectorRestrictionType type;
    private Boolean activated;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

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

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}
