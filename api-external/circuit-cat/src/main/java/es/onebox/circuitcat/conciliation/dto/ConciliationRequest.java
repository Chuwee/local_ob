package es.onebox.circuitcat.conciliation.dto;

import es.onebox.circuitcat.common.dto.Seat;

import java.util.ArrayList;
import java.util.List;

public class ConciliationRequest {
    List<Long> sessionIds = new ArrayList<>();
    List<Seat> seats = new ArrayList<>();

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
