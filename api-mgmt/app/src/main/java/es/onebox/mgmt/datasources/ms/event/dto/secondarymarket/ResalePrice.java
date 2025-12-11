package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;

import java.io.Serializable;

public class ResalePrice implements Serializable {
    private ResalePriceType type;
    private Restrictions restrictions;

    public ResalePriceType getType() {
        return type;
    }

    public void setType(ResalePriceType type) {
        this.type = type;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }
}
