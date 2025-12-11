package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serial;
import java.io.Serializable;

public class PresaleLoyaltyProgram implements Serializable {

    @Serial
    private static final long serialVersionUID = 3746240407100754861L;

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
