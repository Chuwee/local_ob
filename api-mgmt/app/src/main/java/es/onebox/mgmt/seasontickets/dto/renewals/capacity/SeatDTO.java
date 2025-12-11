package es.onebox.mgmt.seasontickets.dto.renewals.capacity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeatDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("seat_id")
    private Long seatId;
    @JsonProperty("seat_name")
    private String seatName;

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }
}
