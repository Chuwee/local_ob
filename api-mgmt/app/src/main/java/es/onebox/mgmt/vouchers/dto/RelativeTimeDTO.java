package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.RelativeTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

@RelativeTime
public class RelativeTimeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private RelativeTimeTypeDTO type;
    @JsonProperty("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime fixedDate;
    @JsonProperty("amount")
    @Min(value = 1, message = "amount must be bigger than 0")
    private Long relativeAmount;
    @JsonProperty("time_period")
    private RelativeTimeTimePeriodDTO timePeriod;

    public RelativeTimeTypeDTO getType() {
        return type;
    }

    public void setType(RelativeTimeTypeDTO type) {
        this.type = type;
    }

    public ZonedDateTime getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(ZonedDateTime fixedDate) {
        this.fixedDate = fixedDate;
    }

    public Long getRelativeAmount() {
        return relativeAmount;
    }

    public void setRelativeAmount(Long relativeAmount) {
        this.relativeAmount = relativeAmount;
    }

    public RelativeTimeTimePeriodDTO getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(RelativeTimeTimePeriodDTO timePeriod) {
        this.timePeriod = timePeriod;
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
