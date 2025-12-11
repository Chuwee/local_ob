package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class UpdateDateFilterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "The enabled field cannot be null")
    @JsonProperty("enabled")
    private Boolean dateFilterEnabled;
    @Valid
    private List<AccessUsersDatesFilterDTO> access;
    @JsonProperty("default_access")
    private ZonedDateTime defaultAccess;

    public Boolean getDateFilterEnabled() { return dateFilterEnabled; }

    public void setDateFilterEnabled(Boolean dateFilterEnabled) { this.dateFilterEnabled = dateFilterEnabled; }

    public List<AccessUsersDatesFilterDTO> getAccess() { return access; }

    public void setAccess(List<AccessUsersDatesFilterDTO> access) { this.access = access; }

    public ZonedDateTime getDefaultAccess() { return defaultAccess; }

    public void setDefaultAccess(ZonedDateTime defaultAccess) { this.defaultAccess = defaultAccess; }
}
