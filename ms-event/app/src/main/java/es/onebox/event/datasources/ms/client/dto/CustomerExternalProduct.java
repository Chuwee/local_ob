package es.onebox.event.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class CustomerExternalProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String eventId;
    private ExternalSeatType seatType;
    private String sectorName;
    private String notNumberedZoneName;
    private String rowName;
    private String seatName;
    private String priceZoneName;
    private String rateName;
    private String purchaseDate;
    private Boolean autoRenewal;
    private String iban;
    private String bic;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ExternalSeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(ExternalSeatType seatType) {
        this.seatType = seatType;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getNotNumberedZoneName() {
        return notNumberedZoneName;
    }

    public void setNotNumberedZoneName(String notNumberedZoneName) {
        this.notNumberedZoneName = notNumberedZoneName;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getPriceZoneName() {
        return priceZoneName;
    }

    public void setPriceZoneName(String priceZoneName) {
        this.priceZoneName = priceZoneName;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
