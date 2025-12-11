package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SettingsInteractiveVenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("allow_interactive_venue")
    private Boolean allowInteractiveVenue;

    @JsonProperty("interactive_venue_type")
    private InteractiveVenueType interactiveVenueType;

    @JsonProperty("allow_venue_3d_view")
    private Boolean allowVenue3dView;

    @JsonProperty("allow_sector_3d_view")
    private Boolean allowSector3dView;

    @JsonProperty("allow_seat_3d_view")
    private Boolean allowSeat3dView;

    public Boolean getAllowInteractiveVenue() {
        return allowInteractiveVenue;
    }

    public void setAllowInteractiveVenue(Boolean allowInteractiveVenue) {
        this.allowInteractiveVenue = allowInteractiveVenue;
    }

    public InteractiveVenueType getInteractiveVenueType() {
        return interactiveVenueType;
    }

    public void setInteractiveVenueType(InteractiveVenueType interactiveVenueType) {
        this.interactiveVenueType = interactiveVenueType;
    }

    public @NotNull(message = "allow_sector_3d_view cannot be null") Boolean getAllowVenue3dView() {
        return allowVenue3dView;
    }

    public void setAllowVenue3dView(@NotNull(message = "allow_sector_3d_view cannot be null") Boolean allowVenue3dView) {
        this.allowVenue3dView = allowVenue3dView;
    }

    public Boolean getAllowSector3dView() {
        return allowSector3dView;
    }

    public void setAllowSector3dView(Boolean allowSector3dView) {
        this.allowSector3dView = allowSector3dView;
    }

    public Boolean getAllowSeat3dView() {
        return allowSeat3dView;
    }

    public void setAllowSeat3dView(Boolean allowSeat3dView) {
        this.allowSeat3dView = allowSeat3dView;
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
