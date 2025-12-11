package es.onebox.event.events.dto;

import org.springframework.format.annotation.DateTimeFormat;

import es.onebox.event.events.enums.BookingExpirationType;
import es.onebox.event.events.enums.BookingOrderExpiration;
import es.onebox.event.events.enums.BookingOrderTimespan;
import es.onebox.event.events.enums.BookingSessionExpiration;
import es.onebox.event.events.enums.BookingSessionTimespan;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BookingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;

    // Order expire
    private BookingOrderExpiration orderExpirationType;
    private BookingOrderTimespan orderExpirationTimespan;
    private Integer orderExpirationTimespanAmount;
    private Integer orderExpirationHour;

    // Type deadline (session and fixed)
    private BookingExpirationType expirationType;
    private BookingSessionTimespan sessionExpirationTimespan;
    private Integer sessionExpirationTimespanAmount;
    private BookingSessionExpiration sessionExpirationType;
    private Integer sessionExpirationHour;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)

    private ZonedDateTime fixedDate;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public BookingOrderExpiration getOrderExpirationType() {
        return orderExpirationType;
    }

    public void setOrderExpirationType(BookingOrderExpiration orderExpirationType) {
        this.orderExpirationType = orderExpirationType;
    }

    public BookingOrderTimespan getOrderExpirationTimespan() {
        return orderExpirationTimespan;
    }

    public void setOrderExpirationTimespan(BookingOrderTimespan orderExpirationTimespan) {
        this.orderExpirationTimespan = orderExpirationTimespan;
    }

    public Integer getOrderExpirationTimespanAmount() {
        return orderExpirationTimespanAmount;
    }

    public void setOrderExpirationTimespanAmount(Integer orderExpirationTimespanAmount) {
        this.orderExpirationTimespanAmount = orderExpirationTimespanAmount;
    }

    public Integer getOrderExpirationHour() {
        return orderExpirationHour;
    }

    public void setOrderExpirationHour(Integer orderExpirationHour) {
        this.orderExpirationHour = orderExpirationHour;
    }

    public BookingExpirationType getExpirationType() {
        return expirationType;
    }

    public void setExpirationType(BookingExpirationType expirationType) {
        this.expirationType = expirationType;
    }

    public BookingSessionTimespan getSessionExpirationTimespan() {
        return sessionExpirationTimespan;
    }

    public void setSessionExpirationTimespan(BookingSessionTimespan sessionExpirationTimespan) {
        this.sessionExpirationTimespan = sessionExpirationTimespan;
    }

    public Integer getSessionExpirationTimespanAmount() {
        return sessionExpirationTimespanAmount;
    }

    public void setSessionExpirationTimespanAmount(Integer sessionExpirationTimespanAmount) {
        this.sessionExpirationTimespanAmount = sessionExpirationTimespanAmount;
    }

    public BookingSessionExpiration getSessionExpirationType() {
        return sessionExpirationType;
    }

    public void setSessionExpirationType(BookingSessionExpiration sessionExpirationType) {
        this.sessionExpirationType = sessionExpirationType;
    }

    public ZonedDateTime getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(ZonedDateTime fixedDate) {
        this.fixedDate = fixedDate;
    }

    public Integer getSessionExpirationHour() {
        return sessionExpirationHour;
    }

    public void setSessionExpirationHour(Integer sessionExpirationHour) {
        this.sessionExpirationHour = sessionExpirationHour;
    }
}
