package es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals;

import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;

import java.io.Serializable;

public class RenewalSeasonTicketRenewalSeat implements Serializable {

    private static final long serialVersionUID = 2249379358939741456L;
    private Long originSeatId;

    private Long renewalSeatId;
    private Integer renewalSectorId;
    private Integer renewalRowId;
    private Long renewalPriceZoneId;
    private SeasonTicketSeatType seatType;
    private Integer renewalNotNumberedZoneId;

    public Long getOriginSeatId() {
        return originSeatId;
    }

    public void setOriginSeatId(Long originSeatId) {
        this.originSeatId = originSeatId;
    }

    public Long getRenewalSeatId() {
        return renewalSeatId;
    }

    public void setRenewalSeatId(Long renewalSeatId) {
        this.renewalSeatId = renewalSeatId;
    }

    public Integer getRenewalSectorId() {
        return renewalSectorId;
    }

    public void setRenewalSectorId(Integer renewalSectorId) {
        this.renewalSectorId = renewalSectorId;
    }

    public Integer getRenewalRowId() {
        return renewalRowId;
    }

    public void setRenewalRowId(Integer renewalRowId) {
        this.renewalRowId = renewalRowId;
    }

    public Long getRenewalPriceZoneId() {
        return renewalPriceZoneId;
    }

    public void setRenewalPriceZoneId(Long renewalPriceZoneId) {
        this.renewalPriceZoneId = renewalPriceZoneId;
    }

    public SeasonTicketSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeasonTicketSeatType seatType) {
        this.seatType = seatType;
    }

    public Integer getRenewalNotNumberedZoneId() {
        return renewalNotNumberedZoneId;
    }

    public void setRenewalNotNumberedZoneId(Integer renewalNotNumberedZoneId) {
        this.renewalNotNumberedZoneId = renewalNotNumberedZoneId;
    }
}