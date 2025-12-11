package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.temporal.ChronoUnit;

public class EventChangeSeatExpiryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9003034118761312076L;

    @JsonProperty("time_offset_limit_unit")
    @NotNull(message = "time_offset_limit_unit cannot be null")
    private ChronoUnit timeOffsetLimitUnit;

    @Positive
    @JsonProperty("time_offset_limit_amount")
    @NotNull(message = "time_offset_limit_amount cannot be null")
    private Integer timeOffsetLimitAmount;

    public ChronoUnit getTimeOffsetLimitUnit() {
        return timeOffsetLimitUnit;
    }

    public void setTimeOffsetLimitUnit(ChronoUnit timeOffsetLimitUnit) {
        this.timeOffsetLimitUnit = timeOffsetLimitUnit;
    }

    public Integer getTimeOffsetLimitAmount() {
        return timeOffsetLimitAmount;
    }

    public void setTimeOffsetLimitAmount(Integer timeOffsetLimitAmount) {
        this.timeOffsetLimitAmount = timeOffsetLimitAmount;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
