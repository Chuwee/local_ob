package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ValidityPeriodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2456615147898703059L;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

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
