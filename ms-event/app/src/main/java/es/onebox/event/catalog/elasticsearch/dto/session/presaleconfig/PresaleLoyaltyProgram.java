package es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig;

import java.io.Serial;
import java.io.Serializable;

public class PresaleLoyaltyProgram implements Serializable {

    @Serial
    private static final long serialVersionUID = 1041897480094005944L;

    private Boolean enabled;
    private Long points;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
