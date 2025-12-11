package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SettingsInteractiveVenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "enabled cannot be null")
    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("allowed_venues")
    private List<@NotNull InteractiveVenueType> allowedVenues;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<InteractiveVenueType> getAllowedVenues() {
        return allowedVenues;
    }

    public void setAllowedVenues(List<InteractiveVenueType> allowedVenues) {
        this.allowedVenues = allowedVenues;
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
