package es.onebox.event.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class UpdateRenewalRequestItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "userId is required")
    private String userId;
    @NotNull(message = "id is required")
    private String id;
    private Long seatId;
    private Long rateId;
    private String renewalSubstatus;
    private Boolean autoRenewal;

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

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
}
