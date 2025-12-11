package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import es.onebox.mgmt.channels.enums.VenueMapNavigationMode;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelVenuemap implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    private VenueMapNavigationMode navigationMode;
    private Boolean showAvailableTickets;
    private Boolean allowPriceRangeTickets;
    private Boolean showImagesCarousel;
    private Boolean enabledAutomaticSelection;
    private Integer preselectedItems;
    private Boolean showCompactedViewList;
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

    public Integer getPreselectedItems() {
        return preselectedItems;
    }

    public void setPreselectedItems(Integer preselectedItems) {
        this.preselectedItems = preselectedItems;
    }


    public Boolean getShowCompactedViewList() { return showCompactedViewList; }

    public void setShowCompactedViewList(Boolean showCompactedViewList) { this.showCompactedViewList = showCompactedViewList; }

    public Boolean getForceSidePanel() {
        return forceSidePanel;
    }

    public void setForceSidePanel(Boolean forceSidePanel) {
        this.forceSidePanel = forceSidePanel;
    }
}
