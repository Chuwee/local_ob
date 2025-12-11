package es.onebox.event.sessions.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class AdditionalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Integer> activeCustomerTypes;

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

    public List<Integer> getActiveCustomerTypes() {
        return activeCustomerTypes;
    }

    public void setActiveCustomerTypes(List<Integer> activeCustomerTypes) {
        this.activeCustomerTypes = activeCustomerTypes;
    }
}
