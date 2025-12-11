package es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;

import java.io.Serializable;

public class RenewalSeasonTicketOriginSeat implements Serializable, RenewalSeat {
    private static final long serialVersionUID = -2532565712618668723L;

    @JsonIgnore
    private String userId;

    private Integer originSectorId;
    private Integer originRowId;
    private Long originSeatId;
    private String originSectorName;
    private String originRowName;
    private String originSeatName;
    private SeasonTicketSeatType seatType;
    private Integer originNotNumberedZoneId;
    private String originNotNumberedZoneName;
    private Double balance;
    private Boolean autoRenewal;
    private String iban;
    private String bic;

    @JsonIgnore
    private Long priceZoneId;

    @JsonIgnore
    private Long rateId;

    @Override
    public Long getSeatId() {
        return getOriginSeatId();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOriginSectorId() {
        return originSectorId;
    }

    public void setOriginSectorId(Integer originSectorId) {
        this.originSectorId = originSectorId;
    }

    public Integer getOriginRowId() {
        return originRowId;
    }

    public void setOriginRowId(Integer originRowId) {
        this.originRowId = originRowId;
    }

    public Long getOriginSeatId() {
        return originSeatId;
    }

    public void setOriginSeatId(Long originSeatId) {
        this.originSeatId = originSeatId;
    }

    public String getOriginSectorName() {
        return originSectorName;
    }

    public void setOriginSectorName(String originSectorName) {
        this.originSectorName = originSectorName;
    }

    public String getOriginRowName() {
        return originRowName;
    }

    public void setOriginRowName(String originRowName) {
        this.originRowName = originRowName;
    }

    public String getOriginSeatName() {
        return originSeatName;
    }

    public void setOriginSeatName(String originSeatName) {
        this.originSeatName = originSeatName;
    }

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public SeasonTicketSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeasonTicketSeatType seatType) {
        this.seatType = seatType;
    }

    public Integer getOriginNotNumberedZoneId() {
        return originNotNumberedZoneId;
    }

    public void setOriginNotNumberedZoneId(Integer originNotNumberedZoneId) {
        this.originNotNumberedZoneId = originNotNumberedZoneId;
    }

    public String getOriginNotNumberedZoneName() {
        return originNotNumberedZoneName;
    }

    public void setOriginNotNumberedZoneName(String originNotNumberedZoneName) {
        this.originNotNumberedZoneName = originNotNumberedZoneName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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
}
