package es.onebox.event.events.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.temporal.ChronoUnit;

public class ChangeSeatExpiryTimeDTO {

    @NotNull
    private ChronoUnit timeOffsetLimitUnit;

    @NotNull
    @Positive
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
}
