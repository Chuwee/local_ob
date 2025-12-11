package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelVenueContentLayout;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelPurchaseConfigVenueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("allow_interactive_venue")
    private Boolean allowInteractiveVenue;
    @JsonProperty("interactive_venue_types")
    private List<InteractiveVenueType> interactiveVenueTypes;
    @JsonProperty("allow_venue_3d_view")
    private Boolean allowVenue3dView;
    @JsonProperty("allow_sector_3d_view")
    private Boolean allowSector3dView;
    @JsonProperty("allow_seat_3d_view")
    private Boolean allowSeat3dView;
    @JsonProperty("content_layout")
    private ChannelVenueContentLayout channelVenueContentLayout;

    public Boolean getAllowInteractiveVenue() {
        return allowInteractiveVenue;
    }

    public void setAllowInteractiveVenue(Boolean allowInteractiveVenue) {
        this.allowInteractiveVenue = allowInteractiveVenue;
    }

    public List<InteractiveVenueType> getInteractiveVenueTypes() {
        return interactiveVenueTypes;
    }

    public void setInteractiveVenueTypes(List<InteractiveVenueType> interactiveVenueTypes) {
        this.interactiveVenueTypes = interactiveVenueTypes;
    }

    public Boolean getAllowVenue3dView() {
        return allowVenue3dView;
    }

    public void setAllowVenue3dView(Boolean allowVenue3dView) {
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

    public ChannelVenueContentLayout getChannelVenueContentLayout() {
        return channelVenueContentLayout;
    }

    public void setChannelVenueContentLayout(ChannelVenueContentLayout channelVenueContentLayout) {
        this.channelVenueContentLayout = channelVenueContentLayout;
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
