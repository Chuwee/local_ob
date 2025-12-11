package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionDynamicPriceConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean active;
    private List<DynamicPriceZone> dynamicPriceZone;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<DynamicPriceZone> getDynamicPriceZone() {
        return dynamicPriceZone;
    }

    public void setDynamicPriceZone(List<DynamicPriceZone> dynamicPriceZone) {
        this.dynamicPriceZone = dynamicPriceZone;
    }
}
