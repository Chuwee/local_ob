package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.VenueMapNavigationMode;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelVenuemapDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    @JsonProperty("navigation_mode")
    private VenueMapNavigationMode navigationMode;
    @JsonProperty("show_available_tickets")
    private Boolean showAvailableTickets;
    @JsonProperty("allow_price_range_filter")
    private Boolean allowPriceRangeTickets;
    @JsonProperty("show_images_carousel")
    private Boolean showImagesCarousel;
    @JsonProperty("enabled_automatic_selection")
    private Boolean enabledAutomaticSelection;
    @JsonProperty("preselected_items")
    private Integer preselectedItems;
    @JsonProperty("show_compacted_view_list")
    private Boolean showCompactedViewList;
    @JsonProperty("force_side_panel_view_list")
    private Boolean forceSidePanel;

    public VenueMapNavigationMode getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(VenueMapNavigationMode navigationMode) {
        this.navigationMode = navigationMode;
    }

    public Boolean getShowAvailableTickets() {
        return showAvailableTickets;
    }

    public void setShowAvailableTickets(Boolean showAvailableTickets) {
        this.showAvailableTickets = showAvailableTickets;
    }

    public Boolean getAllowPriceRangeTickets() {
        return allowPriceRangeTickets;
    }

    public void setAllowPriceRangeTickets(Boolean allowPriceRangeTickets) {
        this.allowPriceRangeTickets = allowPriceRangeTickets;
    }

    public Boolean getShowImagesCarousel() {
        return showImagesCarousel;
    }

    public void setShowImagesCarousel(Boolean showImagesCarousel) {
        this.showImagesCarousel = showImagesCarousel;
    }

    public Boolean getEnabledAutomaticSelection() {
        return enabledAutomaticSelection;
    }

    public void setEnabledAutomaticSelection(Boolean enabledAutomaticSelection) {
        this.enabledAutomaticSelection = enabledAutomaticSelection;
    }

    public Integer getPreselectedItems() {return preselectedItems;}

    public void setPreselectedItems(Integer preselectedItems) {this.preselectedItems = preselectedItems;}


    public Boolean getShowCompactedViewList() { return showCompactedViewList; }

    public void setShowCompactedViewList(Boolean showCompactedViewList) { this.showCompactedViewList = showCompactedViewList; }

    public Boolean getForceSidePanel() {
        return forceSidePanel;
    }

    public void setForceSidePanel(Boolean forceSidePanel) {
        this.forceSidePanel = forceSidePanel;
    }
}
