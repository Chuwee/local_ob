package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.event.dto.event.TypeOrderExpire;
import es.onebox.mgmt.events.enums.TimespanBookingOrderExpiration;

import java.io.Serializable;

public class BookingOrderExpiration implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("timespan")
    private TimespanBookingOrderExpiration timespan;

    @JsonProperty("timespan_amount")
    private Integer timespanAmount;

    @JsonProperty("expiration_time")
    private Integer expirationTime;

    @JsonProperty("expiration_type")
    private TypeOrderExpire orderExpirationType;

    public TimespanBookingOrderExpiration getTimespan() {
        return timespan;
    }

    public void setTimespan(TimespanBookingOrderExpiration timespan) {
        this.timespan = timespan;
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

    public TypeOrderExpire getOrderExpirationType() {
        return orderExpirationType;
    }

    public void setOrderExpirationType(TypeOrderExpire orderExpirationType) {
        this.orderExpirationType = orderExpirationType;
    }
}
