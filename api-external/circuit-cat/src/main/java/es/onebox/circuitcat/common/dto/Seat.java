package es.onebox.circuitcat.common.dto;

import es.onebox.common.datasources.common.dto.SeatStatus;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Objects;

public class Seat implements Serializable {

    private static final long serialVersionUID = -7523976240307330173L;

    private String sector;
    private String row;
    private String seat;
    private SeatStatus status;

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
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

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seat seat1 = (Seat) o;
        return Objects.equals(getSector(), seat1.getSector()) && Objects.equals(getRow(), seat1.getRow()) && Objects.equals(getSeat(), seat1.getSeat());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
