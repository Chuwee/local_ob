package es.onebox.mgmt.users.dto.ratelimit;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.users.enums.ratelimit.TimeUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UserRateLimitQuotaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "time_unit must not be null")
    @JsonProperty("time_unit")
    private TimeUnit timeUnit;
    @NotNull(message = "period must not be null")
    @Min(value = 0, message = "period must be greater than or equal to 0")
    private Integer period;
    @NotNull(message = "limit must not be null")
    @Min(value = 0, message = "limit must be greater than or equal to 0")
    private Integer limit;

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
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
