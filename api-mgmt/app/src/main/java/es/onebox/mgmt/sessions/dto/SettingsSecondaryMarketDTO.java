package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;

import java.io.Serial;
import java.io.Serializable;

public class SettingsSecondaryMarketDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("start_date")
    private ZonedDateTimeWithRelative startDate;

    @JsonProperty("end_date")
    private ZonedDateTimeWithRelative endDate;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ZonedDateTimeWithRelative getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTimeWithRelative startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTimeWithRelative getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTimeWithRelative endDate) {
        this.endDate = endDate;
    }
}
