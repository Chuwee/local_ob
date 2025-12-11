package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.RenewalType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class UpdateRenewalRequestItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("user_id")
    @NotNull(message = "user_id is required")
    private String userId;

    @NotNull(message = "id is required")
    private String id;

    @JsonProperty("seat_id")
    private Long seatId;

    @JsonProperty("rate_id")
    private Long rateId;

    @JsonProperty("renewal_substatus")
    private String renewalSubstatus;

    @JsonProperty("auto_renewal")
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

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
}
