package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;
import java.io.Serializable;

public class VenueTagCounterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer sourceStatus;
    private Integer sourceBlockingReason;
    private Integer sourceQuota;
    private Integer counter;

    public VenueTagCounterDTO() {
    }

    public VenueTagCounterDTO(Integer id, Integer counter) {
        this.id = id;
        this.counter = counter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(Integer sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public Integer getSourceBlockingReason() {
        return sourceBlockingReason;
    }

    public void setSourceBlockingReason(Integer sourceBlockingReason) {
        this.sourceBlockingReason = sourceBlockingReason;
    }

    public Integer getSourceQuota() {
        return sourceQuota;
    }

    public void setSourceQuota(Integer sourceQuota) {
        this.sourceQuota = sourceQuota;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
