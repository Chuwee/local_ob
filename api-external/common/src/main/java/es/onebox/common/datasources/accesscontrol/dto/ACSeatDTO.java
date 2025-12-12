package es.onebox.common.datasources.accesscontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ACSeatDTO implements Serializable {

    private static final long serialVersionUID = 2610175829349386978L;
    private SeatType type;
    private String row;
    private String seat;
    private SeatGateDTO gate;
    private SeatSectorDTO sector;
    @JsonProperty("not_numbered_zone")
    private SeatNotNumberedZoneDTO notNumberedZone;

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

    public SeatGateDTO getGate() {
        return gate;
    }

    public void setGate(SeatGateDTO gate) {
        this.gate = gate;
    }

    public SeatSectorDTO getSector() {
        return sector;
    }

    public void setSector(SeatSectorDTO sector) {
        this.sector = sector;
    }

    public SeatNotNumberedZoneDTO getNotNumberedZone() {
        return notNumberedZone;
    }

    public void setNotNumberedZone(SeatNotNumberedZoneDTO notNumberedZone) {
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
