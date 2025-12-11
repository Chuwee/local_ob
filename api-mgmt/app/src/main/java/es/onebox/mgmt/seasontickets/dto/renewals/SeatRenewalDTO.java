package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeatRenewalDTO implements Serializable {
    private static final long serialVersionUID = -1L;


    @JsonProperty("seat_type")
    private SeasonTicketSeatType seatType;
    @JsonProperty("not_numbered_zone_id")
    private Long notNumberedZoneId;
    @JsonProperty("sector_id")
    private Long sectorId;
    @JsonProperty("row_id")
    private Long rowId;
    @JsonProperty("seat_id")
    private Long seatId;
    private String sector;
    private String row;
    private String seat;
    @JsonProperty("price_zone")
    private String prizeZone;
    @JsonProperty("not_numbered_zone")
    private String notNumberedZone;

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

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

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

    public String getPrizeZone() {
        return prizeZone;
    }

    public void setPrizeZone(String prizeZone) {
        this.prizeZone = prizeZone;
    }

    public String getNotNumberedZone() {
        return notNumberedZone;
    }

    public void setNotNumberedZone(String notNumberedZone) {
        this.notNumberedZone = notNumberedZone;
    }
}
