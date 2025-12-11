package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class RelatedRateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("old_rate_id")
    @NotNull
    private Long oldRateId;

    @JsonProperty("new_rate_id")
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
