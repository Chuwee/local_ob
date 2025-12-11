package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;

    // Order expire
    private TypeOrderExpire orderExpirationType;
    private TimespanOrderExpire orderExpirationTimespan;
    private Integer orderExpirationTimespanAmount;
    private Integer orderExpirationHour;

    // Type deadline (session and fixed)
    private TypeDeadlineExpiration expirationType;
    private TimespanSessionExpire sessionExpirationTimespan;
    private Integer sessionExpirationTimespanAmount;
    private SessionTypeExpiration sessionExpirationType;
    private Integer sessionExpirationHour;
    private ZonedDateTime fixedDate;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public TypeOrderExpire getOrderExpirationType() {
        return orderExpirationType;
    }

    public void setOrderExpirationType(TypeOrderExpire orderExpirationType) {
        this.orderExpirationType = orderExpirationType;
    }

    public TimespanOrderExpire getOrderExpirationTimespan() {
        return orderExpirationTimespan;
    }

    public void setOrderExpirationTimespan(TimespanOrderExpire orderExpirationTimespan) {
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

    public TypeDeadlineExpiration getExpirationType() {
        return expirationType;
    }

    public void setExpirationType(TypeDeadlineExpiration expirationType) {
        this.expirationType = expirationType;
    }

    public TimespanSessionExpire getSessionExpirationTimespan() {
        return sessionExpirationTimespan;
    }

    public void setSessionExpirationTimespan(TimespanSessionExpire sessionExpirationTimespan) {
        this.sessionExpirationTimespan = sessionExpirationTimespan;
    }

    public Integer getSessionExpirationTimespanAmount() {
        return sessionExpirationTimespanAmount;
    }

    public void setSessionExpirationTimespanAmount(Integer sessionExpirationTimespanAmount) {
        this.sessionExpirationTimespanAmount = sessionExpirationTimespanAmount;
    }

    public SessionTypeExpiration getSessionExpirationType() {
        return sessionExpirationType;
    }

    public void setSessionExpirationType(SessionTypeExpiration sessionExpirationType) {
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
