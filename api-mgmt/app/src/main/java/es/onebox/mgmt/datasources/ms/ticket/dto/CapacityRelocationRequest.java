package es.onebox.mgmt.datasources.ms.ticket.dto;

import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CapacityRelocationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 20)
    private List<SeatRelocation> seats;
    private Long userId;

    public List<SeatRelocation> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatRelocation> seats) {
        this.seats = seats;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
