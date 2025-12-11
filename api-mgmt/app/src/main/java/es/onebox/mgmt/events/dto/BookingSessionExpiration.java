package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.SessionTimeFrame;

import java.io.Serializable;

public class BookingSessionExpiration implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("timespan")
    private SessionTimeFrame sessionTimeFrame;

    @JsonProperty("timespan_amount")
    private Integer timespanAmount;

    @JsonProperty("expiration_time")
    private Integer expirationTime;

    public SessionTimeFrame getSessionTimeFrame() {
        return sessionTimeFrame;
    }

    public void setSessionTimeFrame(SessionTimeFrame sessionTimeFrame) {
        this.sessionTimeFrame = sessionTimeFrame;
    }

    public Integer getTimespanAmount() {
        return timespanAmount;
    }

    public void setTimespanAmount(Integer timespanAmount) {
        this.timespanAmount = timespanAmount;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }
}
