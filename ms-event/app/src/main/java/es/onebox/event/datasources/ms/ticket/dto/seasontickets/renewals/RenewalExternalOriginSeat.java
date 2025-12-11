package es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals;

import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;

import java.io.Serializable;

public class RenewalExternalOriginSeat implements Serializable, RenewalSeat {
    private static final long serialVersionUID = -2532565712618668723L;

    private String userId;
    private Long rateId;
    private String priceZone;
    private SeasonTicketSeatType seatType;
    private String sector;
    private String notNumberedZone;
    private String row;
    private String seat;
    private Boolean autoRenewal;
    private String iban;
    private String bic;

    private Long renewalProcessIdentifier;

    @Override
    public Long getSeatId() {
        return getRenewalProcessIdentifier();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public String getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(String priceZone) {
        this.priceZone = priceZone;
    }

    public SeasonTicketSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeasonTicketSeatType seatType) {
        this.seatType = seatType;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getNotNumberedZone() {
        return notNumberedZone;
    }

    public void setNotNumberedZone(String notNumberedZone) {
        this.notNumberedZone = notNumberedZone;
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

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Long getRenewalProcessIdentifier() {
        return renewalProcessIdentifier;
    }

    public void setRenewalProcessIdentifier(Long renewalProcessIdentifier) {
        this.renewalProcessIdentifier = renewalProcessIdentifier;
    }
}
