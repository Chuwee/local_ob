package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class RelatedRate implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long oldRateId;
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
