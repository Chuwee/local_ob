package es.onebox.event.seasontickets.dao.couch;

import java.io.Serializable;

public class SeasonTicketSeat implements Serializable {
    private static final long serialVersionUID = -7604318401334582765L;

    private SeasonTicketSeatType seatType;
    private Integer notNumberedZoneId;
    private Integer sectorId;
    private Integer rowId;
    private Long seatId;
    private String seatName;
    private Long priceZoneId;

    public SeasonTicketSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeasonTicketSeatType seatType) {
        this.seatType = seatType;
    }

    public Integer getNotNumberedZoneId() {
        return notNumberedZoneId;
    }

    public void setNotNumberedZoneId(Integer notNumberedZoneId) {
        this.notNumberedZoneId = notNumberedZoneId;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

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

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }
}
