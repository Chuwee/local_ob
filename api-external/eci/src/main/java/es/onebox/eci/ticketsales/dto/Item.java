package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 2458655462595762754L;

    @JsonProperty("reservation_for")
    private ReservationFor reservationFor;
    @JsonProperty("reserved_ticket")
    private ReservedTicket reservedTicket;

    public ReservationFor getReservationFor() {
        return reservationFor;
    }

    public void setReservationFor(ReservationFor reservationFor) {
        this.reservationFor = reservationFor;
    }

    public ReservedTicket getReservedTicket() {
        return reservedTicket;
    }

    public void setReservedTicket(ReservedTicket reservedTicket) {
        this.reservedTicket = reservedTicket;
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
