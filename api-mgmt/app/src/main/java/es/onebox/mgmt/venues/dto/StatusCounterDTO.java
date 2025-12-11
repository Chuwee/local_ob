package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.venues.enums.SeatStatus;

import java.io.Serializable;

public class StatusCounterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SeatStatus status;
    private Integer count;

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
