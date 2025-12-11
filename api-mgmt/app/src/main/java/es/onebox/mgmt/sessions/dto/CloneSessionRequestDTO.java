package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CloneSessionRequestDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @NotBlank(message = "name is mandatory")
    @Length(min = 0, max = 50, message = "name length cannot be above 50 characters")
    private String name;

    @NotNull(message = "start_date is mandatory")
    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    @Size(message = "Session reference length cannot be above 100 characters", max = 100)
    private String reference;

    @JsonProperty("session_pack_seats_target")
    private String sessionPackSeatsTarget;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getSessionPackSeatsTarget() {
        return sessionPackSeatsTarget;
    }

    public void setSessionPackSeatsTarget(String sessionPackSeatsTarget) {
        this.sessionPackSeatsTarget = sessionPackSeatsTarget;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
