package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;

public class UpdateRenewalRequestItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String id;
    private Long seatId;
    private Long rateId;
    private String renewalSubstatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public String getRenewalSubstatus() { return renewalSubstatus; }

    public void setRenewalSubstatus(String renewalSubstatus) { this.renewalSubstatus = renewalSubstatus; }
}
