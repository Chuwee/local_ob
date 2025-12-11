package es.onebox.mgmt.sessions.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CapacityRelocationRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 100)
    @NotEmpty
    private List<SeatRelocationDTO> seats;

    public List<SeatRelocationDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatRelocationDTO> seats) {
        this.seats = seats;
    }
}
