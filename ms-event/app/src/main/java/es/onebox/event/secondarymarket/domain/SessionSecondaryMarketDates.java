package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionSecondaryMarketDates implements Serializable {

    @Serial
    private static final long serialVersionUID = -9181236336231072222L;

    private Boolean enabled;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
}
