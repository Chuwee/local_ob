package es.onebox.event.datasources.ms.ticket.dto.secondarymarket;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class Ticket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long seatId;
    private Long notNumberedAreaId;
    private Long priceZoneId;
    private Long viewId;
    private Long sectorId;
    private Long sessionId;
    private Long eventId;
    private Long quotaId;
    private Double originalPrice;
    private Double promoterCharges;
    private Double channelCharges;
    private Double price;
    private Rate rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Long notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
    }

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getQuotaId() { return quotaId; }

    public void setQuotaId(Long quotaId) { this.quotaId = quotaId; }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Double getPromoterCharges() {
        return promoterCharges;
    }

    public void setPromoterCharges(Double promoterCharges) {
        this.promoterCharges = promoterCharges;
    }

    public Double getChannelCharges() {
        return channelCharges;
    }

    public void setChannelCharges(Double channelCharges) {
        this.channelCharges = channelCharges;
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
