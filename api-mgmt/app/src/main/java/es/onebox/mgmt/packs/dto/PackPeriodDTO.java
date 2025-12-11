package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.packs.enums.PackRangeType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PackPeriodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 695924581252762742L;

    private PackRangeType type;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    public PackRangeType getType() {
        return type;
    }

    public void setType(PackRangeType type) {
        this.type = type;
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
