package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SettingsSecondaryMarketDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")

    private ZonedDateTime endDate;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
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
