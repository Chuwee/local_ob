package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SettingsEmailNotificationsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @Min(value = 0, message = "send_limit must be equal or above 0")
    @Max(value = 1000000, message = "send_limit")
    @JsonProperty("send_limit")
    private Long sendLimit;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(Long sendLimit) {
        this.sendLimit = sendLimit;
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
