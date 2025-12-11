package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class UpdateSessionRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -765594781915280230L;

    private Long id;

    private String name;

    private SessionStatus status;

    @JsonProperty("start_date")
    private ZonedDateTime startDate;

    @JsonProperty("end_date")
    private ZonedDateTime endDate;

    @Valid
    private UpdateSessionSettingsDTO settings;

    @Size(message = "Session reference length cannot be above 100 characters", max = 100)
    private String reference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public UpdateSessionSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(UpdateSessionSettingsDTO settings) {
        this.settings = settings;
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
