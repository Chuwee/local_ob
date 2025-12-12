package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class PreSaleConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean active;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
