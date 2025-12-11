package es.onebox.event.seasontickets.dao.dto;

import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;

import java.io.Serializable;

public class SeatRenewalES implements Serializable {
    private static final long serialVersionUID = 1L;

    private SeasonTicketSeatType seatType;
    private Long notNumberedZoneId;
    private String notNumberedZone;
    private Long sectorId;
    private String sector;
    private Long rowId;
    private String row;
    private Long seatId;
    private String seat;
    private Long prizeZoneId;
    private String prizeZone;

    public SeasonTicketSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeasonTicketSeatType seatType) {
        this.seatType = seatType;
    }

    public Long getNotNumberedZoneId() {
        return notNumberedZoneId;
    }

    public void setNotNumberedZoneId(Long notNumberedZoneId) {
        this.notNumberedZoneId = notNumberedZoneId;
    }

    public String getNotNumberedZone() {
        return notNumberedZone;
    }

    public void setNotNumberedZone(String notNumberedZone) {
        this.notNumberedZone = notNumberedZone;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Long getPrizeZoneId() {
        return prizeZoneId;
    }

    public void setPrizeZoneId(Long prizeZoneId) {
        this.prizeZoneId = prizeZoneId;
    }

    public String getPrizeZone() {
        return prizeZone;
    }

    public void setPrizeZone(String prizeZone) {
        this.prizeZone = prizeZone;
    }
}
