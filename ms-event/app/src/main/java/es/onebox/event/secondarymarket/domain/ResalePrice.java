package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;

public class ResalePrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
