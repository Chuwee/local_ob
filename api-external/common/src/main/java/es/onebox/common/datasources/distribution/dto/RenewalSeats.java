package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RenewalSeats implements Serializable {

    @Serial
    private static final long serialVersionUID = -1777813980762799148L;

    @JsonProperty("seats")
    private List<RenewalSeat> renewalSeats;

    public List<RenewalSeat> getRenewalSeats() {
        return renewalSeats;
    }

    public void setRenewalSeats(List<RenewalSeat> renewalSeats) {
        this.renewalSeats = renewalSeats;
    }
}
