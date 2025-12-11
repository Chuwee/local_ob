package es.onebox.event.seasontickets.dto.renewals;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class RelatedRateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long oldRateId;

    @NotNull
    private Long newRateId;

    public Long getOldRateId() {
        return oldRateId;
    }

    public void setOldRateId(Long oldRateId) {
        this.oldRateId = oldRateId;
    }

    public Long getNewRateId() {
        return newRateId;
    }

    public void setNewRateId(Long newRateId) {
        this.newRateId = newRateId;
    }
}
