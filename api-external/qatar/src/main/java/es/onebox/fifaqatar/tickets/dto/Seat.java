package es.onebox.fifaqatar.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Seat implements Serializable {

    private static final long serialVersionUID = 4339095493506490476L;

    private SeatType type;
    private String row;
    private String seat;
    private SeatGate gate;
    private SeatSector sector;
    @JsonProperty("not_numbered_zone")
    private NotNumberedZone notNumberedZone;

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public SeatGate getGate() {
        return gate;
    }

    public void setGate(SeatGate gate) {
        this.gate = gate;
    }

    public SeatSector getSector() {
        return sector;
    }

    public void setSector(SeatSector sector) {
        this.sector = sector;
    }

    public NotNumberedZone getNotNumberedZone() {
        return notNumberedZone;
    }

    public void setNotNumberedZone(NotNumberedZone notNumberedZone) {
        this.notNumberedZone = notNumberedZone;
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
