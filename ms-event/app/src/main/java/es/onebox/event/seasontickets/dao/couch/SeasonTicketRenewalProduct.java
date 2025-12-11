package es.onebox.event.seasontickets.dao.couch;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalProduct implements Serializable {

    private static final long serialVersionUID = 1833288764839601987L;


    private String id;
    private Long originSeasonTicketId;
    private Long originRateId;
    private Long renewalRateId;
    private String orderCode;
    private String refundOrderCode;
    private SeasonTicketRenewalStatus status;
    private ZonedDateTime purchaseDate;
    private Boolean externalOrigin;
    private SeasonTicketSeat originSeasonTicketSeat;
    private SeasonTicketExternalSeat originExternalSeat;
    private SeasonTicketSeat renewalSeasonTicketSeat;
    private Double balance;
    private Boolean autoRenewal;
    private String renewalSubstatus;
    private String iban;
    private String bic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOriginSeasonTicketId() {
        return originSeasonTicketId;
    }

    public void setOriginSeasonTicketId(Long originSeasonTicketId) {
        this.originSeasonTicketId = originSeasonTicketId;
    }

    public SeasonTicketSeat getOriginSeasonTicketSeat() {
        return originSeasonTicketSeat;
    }

    public void setOriginSeasonTicketSeat(SeasonTicketSeat originSeasonTicketSeat) {
        this.originSeasonTicketSeat = originSeasonTicketSeat;
    }

    public SeasonTicketSeat getRenewalSeasonTicketSeat() {
        return renewalSeasonTicketSeat;
    }

    public void setRenewalSeasonTicketSeat(SeasonTicketSeat renewalSeasonTicketSeat) {
        this.renewalSeasonTicketSeat = renewalSeasonTicketSeat;
    }

    public Long getOriginRateId() {
        return originRateId;
    }

    public void setOriginRateId(Long originRateId) {
        this.originRateId = originRateId;
    }

    public Long getRenewalRateId() {
        return renewalRateId;
    }

    public void setRenewalRateId(Long renewalRateId) {
        this.renewalRateId = renewalRateId;
    }

    public SeasonTicketRenewalStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketRenewalStatus status) {
        this.status = status;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getRefundOrderCode() {
        return refundOrderCode;
    }

    public void setRefundOrderCode(String refundOrderCode) {
        this.refundOrderCode = refundOrderCode;
    }

    public SeasonTicketExternalSeat getOriginExternalSeat() {
        return originExternalSeat;
    }

    public void setOriginExternalSeat(SeasonTicketExternalSeat originExternalSeat) {
        this.originExternalSeat = originExternalSeat;
    }

    public Boolean getExternalOrigin() {
        return externalOrigin;
    }

    public void setExternalOrigin(Boolean externalOrigin) {
        this.externalOrigin = externalOrigin;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getAutoRenewal() { return autoRenewal; }

    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }

    public String getRenewalSubstatus() { return renewalSubstatus; }

    public void setRenewalSubstatus(String renewalSubstatus) { this.renewalSubstatus = renewalSubstatus; }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
