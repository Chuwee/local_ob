package es.onebox.common.datasources.distribution.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.Valid;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AddSeatsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8167629901107954775L;
    private List<@Valid SeatDTO> seats;

    public AddSeatsDTO() {
    }

    public List<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
