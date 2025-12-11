package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;

public class ChangeSeatExpiryTime implements Serializable {

    private ChronoUnit timeOffsetLimitUnit;
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
